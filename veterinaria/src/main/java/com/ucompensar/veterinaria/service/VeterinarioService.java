/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucompensar.veterinaria.service;

import com.ucompensar.veterinaria.DTO.VeterinarioDTO;
import com.ucompensar.veterinaria.dao.VeterinarioDAO;
import com.ucompensar.veterinaria.model.entity.Veterinario;
import com.ucompensar.veterinaria.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class VeterinarioService {

    // CREAR
    public void crear(
            Veterinario veterinario,
            Long idUsuario) {

        EntityManager em = JPAUtil.getEntityManager();

        try {

            new VeterinarioDAO(em).crear(veterinario);

            AuditoriaService auditoriaService =
                    new AuditoriaService();

            auditoriaService.registrar(
                    idUsuario,
                    "CREAR",
                    "Veterinario",
                    veterinario.getIdVeterinario(),
                    "Creación de veterinario: "
                    + veterinario.getNombres()
                    + " "
                    + veterinario.getApellidos()
            );

        } finally {
            em.close();
        }
    }

    public Veterinario buscarPorId(Long id) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            return new VeterinarioDAO(em).buscarPorId(id);

        } finally {
            em.close();
        }
    }

    // ACTUALIZAR
    public void actualizar(
            Veterinario veterinario,
            Long idUsuario) {

        EntityManager em = JPAUtil.getEntityManager();

        try {

            new VeterinarioDAO(em).actualizar(veterinario);

            AuditoriaService auditoriaService =
                    new AuditoriaService();

            auditoriaService.registrar(
                    idUsuario,
                    "ACTUALIZAR",
                    "Veterinario",
                    veterinario.getIdVeterinario(),
                    "Actualización de veterinario: "
                    + veterinario.getNombres()
                    + " "
                    + veterinario.getApellidos()
            );

        } finally {
            em.close();
        }
    }

    // ELIMINAR
    public void eliminar(
            Long id,
            Long idUsuario) {

        EntityManager em = JPAUtil.getEntityManager();

        try {

            VeterinarioDAO dao =
                    new VeterinarioDAO(em);

            Veterinario veterinario =
                    dao.buscarPorId(id);

            if (veterinario == null) {
                throw new RuntimeException(
                        "No existe el veterinario con ID: "
                        + id
                );
            }

            String nombreCompleto =
                    veterinario.getNombres()
                    + " "
                    + veterinario.getApellidos();

            dao.eliminar(id);

            AuditoriaService auditoriaService =
                    new AuditoriaService();

            auditoriaService.registrar(
                    idUsuario,
                    "ELIMINAR",
                    "Veterinario",
                    id,
                    "Eliminación de veterinario: "
                    + nombreCompleto
            );

        } finally {
            em.close();
        }
    }

    // LISTADOS
    public List<VeterinarioDTO> listarTodosDTO() {

        EntityManager em = JPAUtil.getEntityManager();

        try {

            List<Veterinario> veterinarios =
                    new VeterinarioDAO(em).listarTodos();

            return veterinarios.stream()
                    .map(v -> new VeterinarioDTO(
                            v.getIdVeterinario(),
                            v.getTipoDocumento(),
                            v.getNumeroDocumento(),
                            v.getNombres(),
                            v.getApellidos(),
                            v.getEspecialidad(),
                            v.getTelefono(),
                            v.getEmail(),
                            v.getEstado()
                    ))
                    .toList();

        } finally {
            em.close();
        }
    }
}