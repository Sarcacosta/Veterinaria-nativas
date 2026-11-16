/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucompensar.veterinaria.dao;

import com.ucompensar.veterinaria.model.entity.UsuarioRol;
import jakarta.persistence.EntityManager;

public class UsuarioRolDAO extends GenericDAO<UsuarioRol, Long> {

    public UsuarioRolDAO(EntityManager em) {
        super(em, UsuarioRol.class);
    }

    // Métodos específicos si es necesario
}

