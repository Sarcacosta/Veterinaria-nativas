/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucompensar.veterinaria.service;

import com.ucompensar.veterinaria.dao.TratamientoDAO;
import com.ucompensar.veterinaria.model.entity.Tratamiento;
import com.ucompensar.veterinaria.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class TratamientoService {
    public void crear(Tratamiento tratamiento) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            new TratamientoDAO(em).crear(tratamiento);
        } finally {
            em.close();
        }
    }

    public Tratamiento buscarPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return new TratamientoDAO(em).buscarPorId(id);
        } finally {
            em.close();
        }
    }
    
    public List<Tratamiento> buscarPorHistorial(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return new TratamientoDAO(em).buscarPorHistorial(id);
        } finally {
            em.close();
        }
    }

    public List<Tratamiento> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return new TratamientoDAO(em).listarTodos();
        } finally {
            em.close();
        }
    }

    public void actualizar(Tratamiento vacunacion) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            new TratamientoDAO(em).actualizar(vacunacion);
        } finally {
            em.close();
        }
    }

    public void eliminar(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            new TratamientoDAO(em).eliminar(id);
        } finally {
            em.close();
        }
    }
}
