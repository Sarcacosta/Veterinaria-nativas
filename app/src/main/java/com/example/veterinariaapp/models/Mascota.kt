package com.example.veterinariaapp.models

data class Mascota(
    val id: Long?,
    val nombre: String?,
    val especie: String?,
    val raza: String?,
    val sexo: String?,
    val fechaNacimiento: String?,
    val color: String?,
    val pesoActual: Double?,
    val idPropietario: Long?,
    val propietario: String?
)