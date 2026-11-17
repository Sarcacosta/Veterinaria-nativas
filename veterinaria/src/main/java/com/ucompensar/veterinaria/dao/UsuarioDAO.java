/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucompensar.veterinaria.dao;

import com.ucompensar.veterinaria.model.entity.Usuario;
import jakarta.persistence.EntityManager;

public class UsuarioDAO extends GenericDAO<Usuario, Long> {

    public UsuarioDAO(EntityManager em) {
        super(em, Usuario.class);
    }

    public Usuario buscarPorUsername(String username) {
        return em.createQuery(
            "SELECT u FROM Usuario u WHERE u.username = :user AND u.estado = true",
            Usuario.class
        ).setParameter("user", username)
         .getResultStream()
         .findFirst()
         .orElse(null);
    }
}

