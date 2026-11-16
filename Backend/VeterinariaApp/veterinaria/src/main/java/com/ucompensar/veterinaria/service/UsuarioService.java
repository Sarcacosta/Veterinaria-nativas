/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucompensar.veterinaria.service;

import com.ucompensar.veterinaria.DTO.UsuarioCompletoDTO;
import com.ucompensar.veterinaria.dao.UsuarioDAO;
import com.ucompensar.veterinaria.model.entity.Usuario;
import com.ucompensar.veterinaria.util.JPAUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

public class UsuarioService {

    // =========================
    // CRUD BÁSICO
    // =========================

    public void crear(Usuario usuario) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            new UsuarioDAO(em).crear(usuario);
        } finally {
            em.close();
        }
    }

    public Usuario buscarPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return new UsuarioDAO(em).buscarPorId(id);
        } finally {
            em.close();
        }
    }

    public Usuario buscarPorUsername(String username) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return new UsuarioDAO(em).buscarPorUsername(username);
        } finally {
            em.close();
        }
    }

    public void actualizar(Usuario usuario) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            new UsuarioDAO(em).actualizar(usuario);
        } finally {
            em.close();
        }
    }

    public void eliminar(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            new UsuarioDAO(em).eliminar(id);
        } finally {
            em.close();
        }
    }

    // =========================
    // MÉTODO PARA FRONTEND (DTO)
    // =========================

    public List<UsuarioCompletoDTO> listarTodosDTO() {

        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Usuario> usuarios = new UsuarioDAO(em).listarTodos();

            return usuarios.stream()
                    .map(u -> new UsuarioCompletoDTO(
                            u.getIdUsuario(),
                            u.getUsername(),
                            u.getNombre(),
                            u.getEmail(),
                            u.getEstado(),
                            u.getRoles().stream()
                                    .map(ur -> ur.getRol().getNombre())
                                    .toList()
                    ))
                    .toList();

        } finally {
            em.close();
        }
    }
}