/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucompensar.veterinaria.model.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "veterinario")
public class Veterinario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVeterinario;

    private String tipoDocumento;
    private String numeroDocumento;
    private String nombres;
    private String apellidos;
    private String especialidad;
    private String telefono;
    private String email;
    private String estado;

    @OneToMany(mappedBy = "veterinario")
    private List<HistorialClinico> historiales;

    // getters y setters

    public Long getIdVeterinario() {
        return idVeterinario;
    }

    public void setIdVeterinario(Long idVeterinario) {
        this.idVeterinario = idVeterinario;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<HistorialClinico> getHistoriales() {
        return historiales;
    }

    public void setHistoriales(List<HistorialClinico> historiales) {
        this.historiales = historiales;
    }
    
}