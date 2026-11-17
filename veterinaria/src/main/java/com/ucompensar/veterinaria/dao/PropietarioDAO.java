/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucompensar.veterinaria.dao;

import com.ucompensar.veterinaria.model.entity.Propietario;
import jakarta.persistence.EntityManager;

public class PropietarioDAO extends GenericDAO<Propietario, Long> {

    public PropietarioDAO(EntityManager em) {
        super(em, Propietario.class);
    }
    
    // Método para contar los propietarios
    public long count() {
        return em.createQuery("SELECT COUNT(p) FROM Propietario p", Long.class)
                 .getSingleResult();
    }
}
