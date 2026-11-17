/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucompensar.veterinaria.DTO;

import java.util.List;

public class UsuarioCompletoDTO {
    private Long idUsuario;
    private String username;
    private String nombre;
    private String email;
    private Boolean estado;
    private List<String> roles;  // Podrías usar el nombre del rol u otro campo simplificado

    // Constructor
    public UsuarioCompletoDTO(Long idUsuario, String username, String nombre, String email, Boolean estado, List<String> roles) {
        this.idUsuario = idUsuario;
        this.username = username;
        this.nombre = nombre;
        this.email = email;
        this.estado = estado;
        this.roles = roles;
    }

    // Getters y Setters
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

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
