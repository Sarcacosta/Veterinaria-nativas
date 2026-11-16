/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucompensar.veterinaria.service;

import com.ucompensar.veterinaria.dao.HistorialClinicoDAO;
import com.ucompensar.veterinaria.model.entity.HistorialClinico;
import com.ucompensar.veterinaria.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class HistorialClinicoService {

    public void crear(HistorialClinico historial) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            new HistorialClinicoDAO(em).crear(historial);
        } finally {
            em.close();
        }
    }

    public HistorialClinico buscarPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return new HistorialClinicoDAO(em).buscarPorId(id);
        } finally {
            em.close();
        }
    }

    public List<HistorialClinico> listarPorMascota(Long idMascota) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return new HistorialClinicoDAO(em).buscarPorMascota(idMascota);
        } finally {
            em.close();
        }
    }

    public void actualizar(HistorialClinico historial) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            new HistorialClinicoDAO(em).actualizar(historial);
        } finally {
            em.close();
        }
    }

    public void eliminar(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            new HistorialClinicoDAO(em).eliminar(id);
        } finally {
            em.close();
        }
    }
}