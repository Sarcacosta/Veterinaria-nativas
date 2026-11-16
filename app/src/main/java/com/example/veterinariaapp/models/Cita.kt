package com.example.veterinariaapp.models



data class Cita(
    val id: Long? = null,
    val mascotaId: Long,
    val veterinarioId: Long,
    val fecha: String,
    val hora: String,
    val motivo: String? = "",
    val estado: String? = "PENDIENTE"
)