/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucompensar.veterinaria.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    @jakarta.persistence.OneToOne
    @jakarta.persistence.JoinColumn(name = "id_propietario", unique = true)
    private Propietario propietario;

    public Propietario getPropietario() { return propietario; }

    public void setPropietario(Propietario propietario) { this.propietario = propietario; }

    @jakarta.persistence.OneToOne
    @jakarta.persistence.JoinColumn(name = "id_veterinario", unique = true)
    private Veterinario veterinario;

    public Veterinario getVeterinario() { return veterinario; }

    private String username;
    private String passwordHash;
    private String nombre;
    private String email;
    private Boolean estado;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<UsuarioRol> roles;

    // getters y setters

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public List<UsuarioRol> getRoles() {
        return roles;
    }

    public void setRoles(List<UsuarioRol> roles) {
        this.roles = roles;
    }
    
}

