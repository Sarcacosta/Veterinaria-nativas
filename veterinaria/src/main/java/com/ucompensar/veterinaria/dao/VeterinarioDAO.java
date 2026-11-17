/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucompensar.veterinaria.dao;

import com.ucompensar.veterinaria.model.entity.Veterinario;
import jakarta.persistence.EntityManager;

public class VeterinarioDAO extends GenericDAO<Veterinario, Long> {

    public VeterinarioDAO(EntityManager em) {
        super(em, Veterinario.class);
    }
    
    // Método para contar los veterinarios
    public long count() {
        return em.createQuery("SELECT COUNT(v) FROM Veterinario v", Long.class)
                 .getSingleResult();
    }
}