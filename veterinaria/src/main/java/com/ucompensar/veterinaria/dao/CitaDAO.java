package com.ucompensar.veterinaria.dao;

import com.ucompensar.veterinaria.model.entity.Cita;
import jakarta.persistence.EntityManager;
import java.util.List;

public class CitaDAO extends GenericDAO<Cita, Long> {

    public CitaDAO(EntityManager em) {
        super(em, Cita.class);
    }

    // RF-05-07: Verificar si el veterinario ya tiene una cita en esa fecha y hora
    public boolean existeDobleReserva(Long veterinarioId, String fecha, String hora) {
        Long count = em.createQuery(
                "SELECT COUNT(c) FROM Cita c WHERE c.veterinarioId = :vetId AND c.fecha = :fecha AND c.hora = :hora AND c.estado <> 'CANCELADA'",
                Long.class)
                .setParameter("vetId", veterinarioId)
                .setParameter("fecha", fecha)
                .setParameter("hora", hora)
                .getSingleResult();
        return count > 0;
    }
    public List<String> obtenerHorasOcupadas(Long veterinarioId, String fecha) {
        return em.createQuery(
                "SELECT c.hora FROM Cita c WHERE c.veterinarioId = :vetId AND c.fecha = :fecha AND c.estado <> 'CANCELADA'",
                String.class)
                .setParameter("vetId", veterinarioId)
                .setParameter("fecha", fecha)
                .getResultList();
    }
    public List<Cita> listarPorVeterinario(Long veterinarioId) {
    return em.createQuery(
        "SELECT c FROM Cita c WHERE c.veterinarioId = :vetId ORDER BY c.fecha, c.hora", 
        Cita.class)
        .setParameter("vetId", veterinarioId)
        .getResultList();
}
    // Verificar si la mascota ya tiene una cita PENDIENTE o REPROGRAMADA
public boolean existeCitaActivaParaMascota(Long mascotaId) {
    Long count = em.createQuery(
        "SELECT COUNT(c) FROM Cita c WHERE c.mascotaId = :mascotaId " +
        "AND c.estado IN ('PENDIENTE', 'REPROGRAMADA')", Long.class)
        .setParameter("mascotaId", mascotaId)
        .getSingleResult();
    return count > 0;
}
// Citas de un veterinario filtradas por fecha
public List<Cita> listarPorVeterinarioYFecha(Long veterinarioId, String fecha) {
    return em.createQuery(
        "SELECT c FROM Cita c WHERE c.veterinarioId = :vetId AND c.fecha = :fecha ORDER BY c.hora", 
        Cita.class)
        .setParameter("vetId", veterinarioId)
        .setParameter("fecha", fecha)
        .getResultList();
}

// Todas las citas filtradas por fecha
public List<Cita> listarTodasPorFecha(String fecha) {
    return em.createQuery(
        "SELECT c FROM Cita c WHERE c.fecha = :fecha ORDER BY c.hora", 
        Cita.class)
        .setParameter("fecha", fecha)
        .getResultList();
}
// Buscar citas por ID de mascota
public List<Cita> listarPorMascota(Long mascotaId) {
    return em.createQuery(
        "SELECT c FROM Cita c WHERE c.mascotaId = :mascotaId ORDER BY c.fecha, c.hora", 
        Cita.class)
        .setParameter("mascotaId", mascotaId)
        .getResultList();
}

// Buscar citas por ID de mascota y fecha
public List<Cita> listarPorMascotaYFecha(Long mascotaId, String fecha) {
    return em.createQuery(
        "SELECT c FROM Cita c WHERE c.mascotaId = :mascotaId AND c.fecha = :fecha ORDER BY c.hora", 
        Cita.class)
        .setParameter("mascotaId", mascotaId)
        .setParameter("fecha", fecha)
        .getResultList();
}
}