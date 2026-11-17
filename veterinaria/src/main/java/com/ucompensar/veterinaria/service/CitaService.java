package com.ucompensar.veterinaria.service;

import com.ucompensar.veterinaria.DTO.CitaDTO;
import com.ucompensar.veterinaria.dao.CitaDAO;
import com.ucompensar.veterinaria.model.entity.Cita;
import com.ucompensar.veterinaria.util.JPAUtil;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

public class CitaService {

    public CitaDTO agendarCita(CitaDTO dto) {
    EntityManager em = JPAUtil.getEntityManager();
    try {
        CitaDAO dao = new CitaDAO(em);

        // 1. Validar que la mascota no tenga ya una cita activa
        if (dao.existeCitaActivaParaMascota(dto.getMascotaId())) {
            throw new IllegalStateException("MASCOTA_CON_CITA");
        }

        // 2. Validar que el veterinario no tenga doble reserva (tu código actual)
        if (dao.existeDobleReserva(dto.getVeterinarioId(), dto.getFecha(), dto.getHora())) {
            throw new IllegalStateException("DOBLE_RESERVA");
        }

        // 3. Guardar la cita
        em.getTransaction().begin();
        Cita cita = new Cita();
        cita.setMascotaId(dto.getMascotaId());
        cita.setVeterinarioId(dto.getVeterinarioId());
        cita.setFecha(dto.getFecha());
        cita.setHora(dto.getHora());
        cita.setMotivo(dto.getMotivo() != null ? dto.getMotivo() : "Consulta General");
        cita.setEstado("PENDIENTE");

        em.persist(cita);
        em.getTransaction().commit();

        return convertirDTO(cita);
    } catch (Exception e) {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        throw e;
    } finally {
        em.close();
    }
}

    public List<CitaDTO> obtenerMisCitas() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Cita> citas = new CitaDAO(em).listarTodos();
            return citas.stream()
                    .map(this::convertirDTO)
                    .collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    public CitaDTO reprogramarCita(CitaDTO dto) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Cita cita = em.find(Cita.class, dto.getId());
            if (cita == null) {
                throw new IllegalArgumentException("Cita no encontrada");
            }
            cita.setFecha(dto.getFecha());
            cita.setHora(dto.getHora());
            if (dto.getMotivo() != null) {
                cita.setMotivo(dto.getMotivo());
            }
            cita.setEstado("REPROGRAMADA");

            Cita actualizada = em.merge(cita);
            em.getTransaction().commit();

            return convertirDTO(actualizada);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
    

    public void cancelarCita(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Cita cita = em.find(Cita.class, id);
            if (cita != null) {
                cita.setEstado("CANCELADA");
                em.merge(cita);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
        
    }

    private CitaDTO convertirDTO(Cita cita) {
        CitaDTO dto = new CitaDTO();
        dto.setId(cita.getId());
        dto.setMascotaId(cita.getMascotaId());
        dto.setVeterinarioId(cita.getVeterinarioId());
        dto.setFecha(cita.getFecha());
        dto.setHora(cita.getHora());
        dto.setMotivo(cita.getMotivo());
        dto.setEstado(cita.getEstado());
        return dto;
    }
    public List<String> obtenerHorasDisponibles(Long veterinarioId, String fecha) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<String> ocupadas = new CitaDAO(em).obtenerHorasOcupadas(veterinarioId, fecha);
            List<String> jornada = List.of(
                "08:00", "09:00", "10:00", "11:00",
                "14:00", "15:00", "16:00", "17:00"
            );
            return jornada.stream()
                    .filter(hora -> !ocupadas.contains(hora))
                    .collect(Collectors.toList());
        } finally {
            em.close();
        }
    }
    public List<CitaDTO> obtenerCitasPorVeterinario(Long veterinarioId) {
    EntityManager em = JPAUtil.getEntityManager();
    try {
        List<Cita> citas = new CitaDAO(em).listarPorVeterinario(veterinarioId);
        return citas.stream().map(this::convertirDTO).collect(Collectors.toList());
    } finally {
        em.close();
    }
}
    public List<CitaDTO> obtenerCitasPorVeterinarioYFecha(Long veterinarioId, String fecha) {
    EntityManager em = JPAUtil.getEntityManager();
    try {
        List<Cita> citas = new CitaDAO(em).listarPorVeterinarioYFecha(veterinarioId, fecha);
        return citas.stream().map(this::convertirDTO).collect(Collectors.toList());
    } finally {
        em.close();
    }
}

public List<CitaDTO> obtenerCitasPorFecha(String fecha) {
    EntityManager em = JPAUtil.getEntityManager();
    try {
        List<Cita> citas = new CitaDAO(em).listarTodasPorFecha(fecha);
        return citas.stream().map(this::convertirDTO).collect(Collectors.toList());
    } finally {
        em.close();
    }
}
public List<CitaDTO> obtenerCitasPorMascota(Long mascotaId) {
    EntityManager em = JPAUtil.getEntityManager();
    try {
        List<Cita> citas = new CitaDAO(em).listarPorMascota(mascotaId);
        return citas.stream().map(this::convertirDTO).collect(Collectors.toList());
    } finally {
        em.close();
    }
}

public List<CitaDTO> obtenerCitasPorMascotaYFecha(Long mascotaId, String fecha) {
    EntityManager em = JPAUtil.getEntityManager();
    try {
        List<Cita> citas = new CitaDAO(em).listarPorMascotaYFecha(mascotaId, fecha);
        return citas.stream().map(this::convertirDTO).collect(Collectors.toList());
    } finally {
        em.close();
    }
}
}