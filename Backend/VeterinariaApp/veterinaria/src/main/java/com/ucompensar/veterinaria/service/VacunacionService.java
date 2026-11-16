/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucompensar.veterinaria.service;

import com.ucompensar.veterinaria.dao.VacunacionDAO;
import com.ucompensar.veterinaria.model.entity.Vacunacion;
import com.ucompensar.veterinaria.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class VacunacionService {
    public void crear(Vacunacion vacunacion) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            new VacunacionDAO(em).crear(vacunacion);
        } finally {
            em.close();
        }
    }

    public Vacunacion buscarPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return new VacunacionDAO(em).buscarPorId(id);
        } finally {
            em.close();
        }
    }
    
    public List<Vacunacion> buscarPorMascota(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return new VacunacionDAO(em).buscarPorMascota(id);
        } finally {
            em.close();
        }
    }

    public List<Vacunacion> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return new VacunacionDAO(em).listarTodos();
        } finally {
            em.close();
        }
    }

    public void actualizar(Vacunacion vacunacion) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            new VacunacionDAO(em).actualizar(vacunacion);
        } finally {
            em.close();
        }
    }

    public void eliminar(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            new VacunacionDAO(em).eliminar(id);
        } finally {
            em.close();
        }
    }
}
