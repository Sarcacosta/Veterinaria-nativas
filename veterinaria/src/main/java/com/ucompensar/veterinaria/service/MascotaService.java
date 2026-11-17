/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucompensar.veterinaria.service;

import com.ucompensar.veterinaria.DTO.MascotaDTO;
import com.ucompensar.veterinaria.dao.MascotaDAO;
import com.ucompensar.veterinaria.model.entity.Mascota;
import com.ucompensar.veterinaria.model.entity.Propietario;
import com.ucompensar.veterinaria.model.entity.Usuario;
import com.ucompensar.veterinaria.model.entity.Auditoria;
import java.time.LocalDateTime;
import com.ucompensar.veterinaria.service.AuthService;
import com.ucompensar.veterinaria.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class MascotaService {

    // =========================
    // MÉTODOS EXISTENTES (OK)
    // =========================

    public void crear(Mascota mascota, Long idUsuario) {
        crear(mascota, idUsuario, false);
    }

    public void crear(Mascota mascota, Long idUsuario, boolean cuentaPropietario) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            Usuario autor = em.find(Usuario.class, idUsuario);
            if (autor == null || !Boolean.TRUE.equals(autor.getEstado())) {
                throw new SecurityException("Cuenta no disponible");
            }
            if (cuentaPropietario && autor.getPropietario() == null) {
                throw new SecurityException("Cuenta sin propietario asociado");
            }
            if (cuentaPropietario) mascota.setPropietario(autor.getPropietario());
            if (mascota.getPropietario() == null || mascota.getPropietario().getIdPropietario() == null) {
                throw new IllegalArgumentException("Selecciona un propietario");
            }
            // Obtener propietario gestionado
            Long idProp = mascota.getPropietario().getIdPropietario();

            Propietario propietario = em.find(
                    Propietario.class,
                    idProp
            );

            if (propietario == null) {
                throw new IllegalArgumentException("Propietario no existe");
            }

            // Asociar propietario gestionado
            mascota.setPropietario(propietario);

            // Persistir mascota
            em.persist(mascota);

            Auditoria auditoria = new Auditoria();
            auditoria.setUsuario(autor);
            auditoria.setAccion("CREAR");
            auditoria.setEntidad("Mascota");
            auditoria.setIdRegistro(mascota.getIdMascota());
            auditoria.setFechaHora(LocalDateTime.now());
            auditoria.setDescripcion("Registro de mascota");
            em.persist(auditoria);
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


    public Mascota buscarPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return new MascotaDAO(em).buscarPorId(id);
        } finally {
            em.close();
        }
    }

    public void actualizar(Mascota mascota, Long idUsuario) {

        EntityManager em = JPAUtil.getEntityManager();

        try {

            new MascotaDAO(em).actualizar(mascota);

            AuditoriaService auditoriaService =
                    new AuditoriaService();

            auditoriaService.registrar(
                    idUsuario,
                    "ACTUALIZAR",
                    "Mascota",
                    mascota.getIdMascota(),
                    "Actualización de mascota: " + mascota.getNombre()
            );

        } finally {
            em.close();
        }
    }

    public void eliminar(Long id, Long idUsuario) {

        EntityManager em = JPAUtil.getEntityManager();

        try {

            MascotaDAO dao = new MascotaDAO(em);

            Mascota mascota = dao.buscarPorId(id);

            if (mascota == null) {
                throw new RuntimeException(
                        "No existe la mascota con ID: " + id
                );
            }

            String nombreMascota = mascota.getNombre();

            dao.eliminar(id);

            AuditoriaService auditoriaService =
                    new AuditoriaService();

            auditoriaService.registrar(
                    idUsuario,
                    "ELIMINAR",
                    "Mascota",
                    id,
                    "Eliminación de mascota: " + nombreMascota
            );

        } finally {
            em.close();
        }
    }

    // =========================
    // NUEVO MÉTODO PARA FRONTEND
    // =========================

    public List<MascotaDTO> listarTodosDTO() {
        return listarDTO(null);
    }

    public List<MascotaDTO> listarDTO(Long usuarioPropietario) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Mascota> mascotas = usuarioPropietario == null
                    ? new MascotaDAO(em).listarTodos()
                    : em.createQuery("SELECT m FROM Mascota m, Usuario u "
                            + "WHERE u.idUsuario = :id AND u.estado = true AND m.propietario = u.propietario",
                            Mascota.class).setParameter("id", usuarioPropietario).getResultList();

            return mascotas.stream()
                    .map(m -> new MascotaDTO(
                            m.getIdMascota(),
                            m.getNombre(),
                            m.getEspecie(),
                            m.getRaza(),
                            m.getSexo(),
                            m.getFechaNacimiento(),
                            m.getColor(),
                            m.getPesoActual(),
                            m.getPropietario().getIdPropietario(),
                            m.getPropietario().getNombres() + " " + m.getPropietario().getApellidos()
                    ))
                    .toList();

        } finally {
            em.close();
        }
    }
}
