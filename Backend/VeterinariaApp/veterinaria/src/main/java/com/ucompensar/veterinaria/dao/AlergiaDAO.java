/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucompensar.veterinaria.dao;

import com.ucompensar.veterinaria.model.entity.Alergia;
import jakarta.persistence.EntityManager;

public class AlergiaDAO extends GenericDAO<Alergia, Long> {

    public AlergiaDAO(EntityManager em) {
        super(em, Alergia.class);
    }
}
