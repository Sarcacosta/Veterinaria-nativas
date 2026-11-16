/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucompensar.veterinaria.dao;

import com.ucompensar.veterinaria.model.entity.Rol;
import jakarta.persistence.EntityManager;

public class RolDAO extends GenericDAO<Rol, Long> {

    public RolDAO(EntityManager em) {
        super(em, Rol.class);
    }

    // Métodos específicos si es necesario
}

