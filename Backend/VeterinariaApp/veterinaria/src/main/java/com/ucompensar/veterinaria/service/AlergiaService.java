/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucompensar.veterinaria.service;

import com.ucompensar.veterinaria.dao.AlergiaDAO;
import com.ucompensar.veterinaria.model.entity.Alergia;
import com.ucompensar.veterinaria.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class AlergiaService {
    public void crear(Alergia alergia) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            new AlergiaDAO(em).crear(alergia);
        } finally {
            em.close();
        }
    }

    public Alergia buscarPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return new AlergiaDAO(em).buscarPorId(id);
        } finally {
            em.close();
        }
    }

    public List<Alergia> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return new AlergiaDAO(em).listarTodos();
        } finally {
            em.close();
        }
    }

    public void actualizar(Alergia medicamento) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            new AlergiaDAO(em).actualizar(medicamento);
        } finally {
            em.close();
        }
    }

    public void eliminar(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            new AlergiaDAO(em).eliminar(id);
        } finally {
            em.close();
        }
    }
}
