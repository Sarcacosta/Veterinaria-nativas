/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucompensar.veterinaria.service;

import com.ucompensar.veterinaria.dao.MedicamentoDAO;
import com.ucompensar.veterinaria.model.entity.Medicamento;
import com.ucompensar.veterinaria.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class MedicamentoService {
    public void crear(Medicamento medicamento) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            new MedicamentoDAO(em).crear(medicamento);
        } finally {
            em.close();
        }
    }

    public Medicamento buscarPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return new MedicamentoDAO(em).buscarPorId(id);
        } finally {
            em.close();
        }
    }

    public List<Medicamento> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return new MedicamentoDAO(em).listarTodos();
        } finally {
            em.close();
        }
    }

    public void actualizar(Medicamento medicamento) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            new MedicamentoDAO(em).actualizar(medicamento);
        } finally {
            em.close();
        }
    }

    public void eliminar(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            new MedicamentoDAO(em).eliminar(id);
        } finally {
            em.close();
        }
    }
}
