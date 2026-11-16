/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucompensar.veterinaria.dao;

import com.ucompensar.veterinaria.model.entity.Vacuna;
import jakarta.persistence.EntityManager;

public class VacunaDAO extends GenericDAO<Vacuna, Long> {

    public VacunaDAO(EntityManager em) {
        super(em, Vacuna.class);
    }
}
