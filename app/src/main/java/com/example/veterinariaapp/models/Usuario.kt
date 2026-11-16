package com.example.veterinariaapp.models

data class Usuario(
    val id: Long?,
    val username: String?,
    val roles: Set<String>? // O List<String> según cómo lo envíe tu backend
)