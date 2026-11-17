/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucompensar.veterinaria.dao;

import com.ucompensar.veterinaria.model.entity.Mascota;
import jakarta.persistence.EntityManager;
import java.util.List;

public class MascotaDAO extends GenericDAO<Mascota, Long> {

    public MascotaDAO(EntityManager em) {
        super(em, Mascota.class);
    }

    public List<Mascota> buscarPorPropietario(Long idPropietario) {
        return em.createQuery(
                "SELECT m FROM Mascota m WHERE m.propietario.idPropietario = :id",
                Mascota.class
        ).setParameter("id", idPropietario)
         .getResultList();
    }
    
    // Método para contar las mascotas
    public long count() {
        return em.createQuery("SELECT COUNT(m) FROM Mascota m", Long.class)
                 .getSingleResult();
    }
}