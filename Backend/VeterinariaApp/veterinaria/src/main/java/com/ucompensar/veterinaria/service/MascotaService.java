/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucompensar.veterinaria.service;

import com.ucompensar.veterinaria.DTO.MascotaDTO;
import com.ucompensar.veterinaria.dao.MascotaDAO;
import com.ucompensar.veterinaria.model.entity.Mascota;
import com.ucompensar.veterinaria.model.entity.Propietario;
import com.ucompensar.veterinaria.service.AuthService;
import com.ucompensar.veterinaria.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class MascotaService {

    // =========================
    // MÉTODOS EXISTENTES (OK)
    // =========================

    public void crear(Mascota mascota, Long idUsuario) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            // Obtener propietario gestionado
            Long idProp = mascota.getPropietario().getIdPropietario();

            Propietario propietario = em.find(
                    Propietario.class,
                    idProp
            );

            if (propietario == null) {
                throw new RuntimeException("Propietario no existe");
            }

            // Asociar propietario gestionado
            mascota.setPropietario(propietario);

            // Persistir mascota
            em.persist(mascota);

            em.getTransaction().commit();

            // Registrar auditoría después de crear correctamente
            AuditoriaService auditoriaService = new AuditoriaService();

            auditoriaService.registrar(
                    idUsuario,
                    "CREAR",
                    "Mascota",
                    mascota.getIdMascota(),
                    "Creación de mascota: " + mascota.getNombre()
            );

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

        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Mascota> mascotas = new MascotaDAO(em).listarTodos();

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
