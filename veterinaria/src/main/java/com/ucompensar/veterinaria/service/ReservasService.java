package com.ucompensar.veterinaria.service;

import com.ucompensar.veterinaria.DTO.CitaDTO;
import com.ucompensar.veterinaria.dao.CitaDAO;
import com.ucompensar.veterinaria.model.entity.*;
import com.ucompensar.veterinaria.util.JPAUtil;
import jakarta.persistence.*;
import java.time.*;
import java.util.List;
import java.util.Objects;

public class ReservasService {
    private final HorarioService horarios = new HorarioService();

    public static class Denegada extends RuntimeException {}
    public static class NoEncontrada extends RuntimeException {}
    public static class Conflicto extends RuntimeException {}

    public List<String> disponibles(long vet, String fecha) {
        validarFecha(fecha);
        try (var em = JPAUtil.getEntityManager()) {
            if (em.find(Veterinario.class, vet) == null) throw new NoEncontrada();
            var ocupadas = new CitaDAO(em).obtenerHorasOcupadas(vet, fecha);
            LocalDateTime ahora = LocalDateTime.now();
            return horarios.jornada(em, vet, fecha).stream()
                    .filter(h -> !ocupadas.contains(h) && LocalDateTime.parse(fecha + "T" + h).isAfter(ahora))
                    .toList();
        }
    }

    // Un propietario se identifica por sesión. Un id nulo representa personal autorizado.
    public CitaDTO guardar(CitaDTO datos, Long propietario, boolean editar) {
        if (datos == null || (editar ? datos.getId() == null : datos.getId() != null
                || datos.getMascotaId() == null || datos.getVeterinarioId() == null)
                || datos.getHora() == null || !datos.getHora().matches("(?:[01][0-9]|2[0-3]):[0-5][0-9]")
                || (datos.getMotivo() != null && datos.getMotivo().length() > 255)) {
            throw new IllegalArgumentException();
        }
        validarFecha(datos.getFecha());
        if (!LocalDateTime.parse(datos.getFecha() + "T" + datos.getHora()).isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException();
        }
        try (var em = JPAUtil.getEntityManager()) {
            var tx = em.getTransaction();
            tx.begin();
            try {
                Cita cita = editar ? buscarCita(em, datos.getId()) : new Cita();
                long mascotaId = editar ? cita.getMascotaId() : datos.getMascotaId();
                long veterinarioId = editar ? cita.getVeterinarioId() : datos.getVeterinarioId();
                Mascota mascota = em.find(Mascota.class, mascotaId, LockModeType.PESSIMISTIC_WRITE);
                if (mascota == null) throw new NoEncontrada();
                autorizar(mascota, propietario);
                if (editar) comprobarEditable(cita);
                horarios.bloquearVeterinario(em, veterinarioId);

                boolean mismoTurno = editar && Objects.equals(cita.getFecha(), datos.getFecha())
                        && Objects.equals(cita.getHora(), datos.getHora());
                CitaDAO dao = new CitaDAO(em);
                if (!mismoTurno) {
                    if (!horarios.jornada(em, veterinarioId, datos.getFecha()).contains(datos.getHora())
                            || dao.existeDobleReserva(veterinarioId, datos.getFecha(), datos.getHora())
                            || mascotaOcupada(em, mascotaId, datos.getFecha(), datos.getHora())) {
                        throw new Conflicto();
                    }
                }

                cita.setMascotaId(mascotaId);
                cita.setVeterinarioId(veterinarioId);
                cita.setFecha(datos.getFecha());
                cita.setHora(datos.getHora());
                if (datos.getMotivo() != null) cita.setMotivo(datos.getMotivo().trim());
                if (cita.getMotivo() == null || cita.getMotivo().isBlank()) cita.setMotivo("Consulta general");
                cita.setEstado(editar ? "REPROGRAMADA" : "PENDIENTE");
                if (!editar) em.persist(cita);
                tx.commit();

                CitaDTO dto = new CitaDTO();
                dto.setId(cita.getId());
                dto.setMascotaId(cita.getMascotaId());
                dto.setVeterinarioId(cita.getVeterinarioId());
                dto.setFecha(cita.getFecha());
                dto.setHora(cita.getHora());
                dto.setMotivo(cita.getMotivo());
                dto.setEstado(cita.getEstado());
                return dto;
            } finally {
                if (tx.isActive()) tx.rollback();
            }
        }
    }

    public void cancelar(long id, Long propietario) {
        try (var em = JPAUtil.getEntityManager()) {
            var tx = em.getTransaction();
            tx.begin();
            try {
                Cita cita = buscarCita(em, id);
                Mascota mascota = em.find(Mascota.class, cita.getMascotaId(), LockModeType.PESSIMISTIC_WRITE);
                if (mascota == null) throw new NoEncontrada();
                autorizar(mascota, propietario);
                comprobarEditable(cita);
                horarios.bloquearVeterinario(em, cita.getVeterinarioId());
                cita.setEstado("CANCELADA");
                tx.commit();
            } finally {
                if (tx.isActive()) tx.rollback();
            }
        }
    }

    private static boolean mascotaOcupada(EntityManager em, long mascota, String fecha, String hora) {
        return em.createQuery("SELECT COUNT(c) FROM Cita c WHERE c.mascotaId = :mascota "
                        + "AND c.fecha = :fecha AND c.hora = :hora AND c.estado <> 'CANCELADA'", Long.class)
                .setParameter("mascota", mascota)
                .setParameter("fecha", fecha)
                .setParameter("hora", hora)
                .getSingleResult() > 0;
    }

    private static Cita buscarCita(EntityManager em, long id) {
        Cita cita = em.find(Cita.class, id, LockModeType.PESSIMISTIC_WRITE);
        if (cita == null) throw new NoEncontrada();
        return cita;
    }

    private static void comprobarEditable(Cita cita) {
        try {
            if ("CANCELADA".equals(cita.getEstado())
                    || !LocalDateTime.parse(cita.getFecha() + "T" + cita.getHora()).isAfter(LocalDateTime.now())) {
                throw new Conflicto();
            }
        } catch (DateTimeException error) { throw new Conflicto(); }
    }

    private static void autorizar(Mascota mascota, Long propietario) {
        if (propietario != null && (mascota.getPropietario() == null
                || !propietario.equals(mascota.getPropietario().getIdPropietario()))) throw new Denegada();
    }

    private static void validarFecha(String fecha) {
        if (fecha == null || !fecha.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) throw new IllegalArgumentException();
        try {
            if (LocalDate.parse(fecha).isBefore(LocalDate.now())) throw new IllegalArgumentException();
        } catch (DateTimeException error) { throw new IllegalArgumentException(); }
    }
}
