/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucompensar.veterinaria.dao;

import com.ucompensar.veterinaria.model.entity.Medicamento;
import jakarta.persistence.EntityManager;

public class MedicamentoDAO extends GenericDAO<Medicamento, Long> {

    public MedicamentoDAO(EntityManager em) {
        super(em, Medicamento.class);
    }
}
