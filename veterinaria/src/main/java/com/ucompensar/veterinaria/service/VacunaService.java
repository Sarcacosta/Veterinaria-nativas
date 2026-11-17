/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucompensar.veterinaria.service;

import com.ucompensar.veterinaria.dao.VacunaDAO;
import com.ucompensar.veterinaria.model.entity.Vacuna;
import com.ucompensar.veterinaria.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class VacunaService {
    public void crear(Vacuna vacuna) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            new VacunaDAO(em).crear(vacuna);
        } finally {
            em.close();
        }
    }

    public Vacuna buscarPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return new VacunaDAO(em).buscarPorId(id);
        } finally {
            em.close();
        }
    }

    public List<Vacuna> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return new VacunaDAO(em).listarTodos();
        } finally {
            em.close();
        }
    }

    public void actualizar(Vacuna historial) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            new VacunaDAO(em).actualizar(historial);
        } finally {
            em.close();
        }
    }

    public void eliminar(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            new VacunaDAO(em).eliminar(id);
        } finally {
            em.close();
        }
    }
}
