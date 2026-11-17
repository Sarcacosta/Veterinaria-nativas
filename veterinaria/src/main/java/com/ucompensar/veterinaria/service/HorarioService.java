package com.ucompensar.veterinaria.service;

import com.ucompensar.veterinaria.dao.CitaDAO;
import com.ucompensar.veterinaria.model.entity.HorarioAtencion;
import com.ucompensar.veterinaria.model.entity.Veterinario;
import com.ucompensar.veterinaria.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.TreeSet;

public class HorarioService {
    private static final List<String> JORNADA_BASE = List.of(
            "08:00", "09:00", "10:00", "11:00", "14:00", "15:00", "16:00", "17:00");

    private HorarioAtencion buscar(EntityManager em, long veterinario, String fecha) {
        return em.createQuery("SELECT h FROM HorarioAtencion h WHERE h.veterinarioId = :vet AND h.fecha = :fecha",
                HorarioAtencion.class).setParameter("vet", veterinario).setParameter("fecha", fecha)
                .getResultStream().findFirst().orElse(null);
    }

    public List<String> jornada(EntityManager em, long veterinario, String fecha) {
        HorarioAtencion horario = buscar(em, veterinario, fecha);
        if (horario == null) return JORNADA_BASE;
        return horario.getHoras().isEmpty() ? List.of() : List.of(horario.getHoras().split(","));
    }

    public void bloquearVeterinario(EntityManager em, long veterinario) {
        if (em.find(Veterinario.class, veterinario, LockModeType.PESSIMISTIC_WRITE) == null) {
            throw new IllegalArgumentException("Veterinario inexistente");
        }
    }

    public List<String> guardar(long veterinario, String fecha, List<String> horas) {
        if (horas.size() > 24 || horas.stream().anyMatch(hora -> hora == null
                || !hora.matches("(?:[01][0-9]|2[0-3]):[0-5][0-9]"))) {
            throw new IllegalArgumentException("Horario no válido");
        }

        List<String> ordenadas = List.copyOf(new TreeSet<>(horas));
        try (var em = JPAUtil.getEntityManager()) {
            var tx = em.getTransaction();
            tx.begin();
            try {
                bloquearVeterinario(em, veterinario);
                if (!ordenadas.containsAll(new CitaDAO(em).obtenerHorasOcupadas(veterinario, fecha))) {
                    throw new IllegalStateException("HORARIO_CON_CITAS");
                }
                HorarioAtencion horario = buscar(em, veterinario, fecha);
                if (horario == null) horario = new HorarioAtencion(veterinario, fecha);
                horario.setHoras(String.join(",", ordenadas));
                em.persist(horario);
                tx.commit();
                return ordenadas;
            } finally {
                if (tx.isActive()) tx.rollback();
            }
        }
    }
}
