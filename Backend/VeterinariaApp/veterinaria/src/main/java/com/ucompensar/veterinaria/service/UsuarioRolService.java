/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucompensar.veterinaria.service;

import com.ucompensar.veterinaria.DTO.UsuarioRolDTO;
import com.ucompensar.veterinaria.dao.UsuarioRolDAO;
import com.ucompensar.veterinaria.model.entity.Rol;
import com.ucompensar.veterinaria.model.entity.Usuario;
import com.ucompensar.veterinaria.model.entity.UsuarioRol;
import com.ucompensar.veterinaria.util.JPAUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

public class UsuarioRolService {

    // =========================
    // ASIGNAR ROL A USUARIO
    // =========================

    public void asignarRol(Long idUsuario, Long idRol) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Usuario usuario = em.find(Usuario.class, idUsuario);
            Rol rol = em.find(Rol.class, idRol);

            if (usuario == null || rol == null) {
                throw new IllegalArgumentException("Usuario o Rol no encontrado");
            }

            UsuarioRol usuarioRol = new UsuarioRol();
            usuarioRol.setUsuario(usuario);
            usuarioRol.setRol(rol);

            new UsuarioRolDAO(em).crear(usuarioRol);

        } finally {
            em.close();
        }
    }

    // =========================
    // ELIMINAR RELACIÓN
    // =========================

    public void eliminar(Long idUsuarioRol) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            new UsuarioRolDAO(em).eliminar(idUsuarioRol);
        } finally {
            em.close();
        }
    }

    // =========================
    // LISTAR RELACIONES (DTO)
    // =========================

    public List<UsuarioRolDTO> listarTodosDTO() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<UsuarioRol> relaciones = new UsuarioRolDAO(em).listarTodos();

            return relaciones.stream()
                    .map(ur -> new UsuarioRolDTO(
                            ur.getId(),
                            ur.getUsuario().getIdUsuario(),
                            ur.getRol().getIdRol()
                    ))
                    .toList();

        } finally {
            em.close();
        }
    }
}