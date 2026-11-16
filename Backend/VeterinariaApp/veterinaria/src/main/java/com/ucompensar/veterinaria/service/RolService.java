/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucompensar.veterinaria.service;

import com.ucompensar.veterinaria.DTO.RolDTO;
import com.ucompensar.veterinaria.dao.RolDAO;
import com.ucompensar.veterinaria.model.entity.Rol;
import com.ucompensar.veterinaria.util.JPAUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

public class RolService {

    // =========================
    // CRUD BÁSICO
    // =========================

    public void crear(Rol rol) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            new RolDAO(em).crear(rol);
        } finally {
            em.close();
        }
    }

    public Rol buscarPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return new RolDAO(em).buscarPorId(id);
        } finally {
            em.close();
        }
    }

    public void actualizar(Rol rol) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            new RolDAO(em).actualizar(rol);
        } finally {
            em.close();
        }
    }

    public void eliminar(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            new RolDAO(em).eliminar(id);
        } finally {
            em.close();
        }
    }

    // =========================
    // DTO PARA FRONTEND
    // =========================

    public List<RolDTO> listarTodosDTO() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Rol> roles = new RolDAO(em).listarTodos();

            return roles.stream()
                    .map(r -> new RolDTO(
                            r.getIdRol(),
                            r.getNombre(),
                            r.getDescripcion()
                    ))
                    .toList();

        } finally {
            em.close();
        }
    }
}