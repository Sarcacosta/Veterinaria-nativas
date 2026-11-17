/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucompensar.veterinaria.service;

import com.ucompensar.veterinaria.DTO.PropietarioDTO;
import com.ucompensar.veterinaria.dao.PropietarioDAO;
import com.ucompensar.veterinaria.model.entity.Propietario;
import com.ucompensar.veterinaria.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class PropietarioService {

    // CREAR
    public void crear(
            Propietario propietario,
            Long idUsuario) {

        EntityManager em = JPAUtil.getEntityManager();

        try {

            new PropietarioDAO(em).crear(propietario);

            AuditoriaService auditoriaService =
                    new AuditoriaService();

            auditoriaService.registrar(
                    idUsuario,
                    "CREAR",
                    "Propietario",
                    propietario.getIdPropietario(),
                    "Creación de propietario: "
                    + propietario.getNombres()
                    + " "
                    + propietario.getApellidos()
            );

        } finally {
            em.close();
        }
    }

    public Propietario buscarPorId(Long id) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            return new PropietarioDAO(em).buscarPorId(id);

        } finally {
            em.close();
        }
    }

    // ACTUALIZAR
    public void actualizar(
            Propietario propietario,
            Long idUsuario) {

        EntityManager em = JPAUtil.getEntityManager();

        try {

            new PropietarioDAO(em).actualizar(propietario);

            AuditoriaService auditoriaService =
                    new AuditoriaService();

            auditoriaService.registrar(
                    idUsuario,
                    "ACTUALIZAR",
                    "Propietario",
                    propietario.getIdPropietario(),
                    "Actualización de propietario: "
                    + propietario.getNombres()
                    + " "
                    + propietario.getApellidos()
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

            PropietarioDAO dao =
                    new PropietarioDAO(em);

            Propietario propietario =
                    dao.buscarPorId(id);

            if (propietario == null) {
                throw new RuntimeException(
                        "No existe el propietario con ID: "
                        + id
                );
            }

            String nombreCompleto =
                    propietario.getNombres()
                    + " "
                    + propietario.getApellidos();

            dao.eliminar(id);

            AuditoriaService auditoriaService =
                    new AuditoriaService();

            auditoriaService.registrar(
                    idUsuario,
                    "ELIMINAR",
                    "Propietario",
                    id,
                    "Eliminación de propietario: "
                    + nombreCompleto
            );

        } finally {
            em.close();
        }
    }

    // LISTADOS
    public List<PropietarioDTO> listarTodosDTO() {

        EntityManager em = JPAUtil.getEntityManager();

        try {

            List<Propietario> propietarios =
                    new PropietarioDAO(em).listarTodos();

            return propietarios.stream()
                    .map(p -> new PropietarioDTO(
                            p.getIdPropietario(),
                            p.getTipoDocumento(),
                            p.getNumeroDocumento(),
                            p.getNombres(),
                            p.getApellidos(),
                            p.getTelefono(),
                            p.getEmail(),
                            p.getDireccion()
                    ))
                    .toList();

        } finally {
            em.close();
        }
    }
}