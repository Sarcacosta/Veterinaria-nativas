package com.example.veterinariaapp.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // Usar 10.0.2.2 para el emulador. (Si usas celular físico, usa tu IP de Wi-Fi).
    private const val BASE_URL = "http://10.100.40.249:8080/veterinaria/"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Instancia genérica para la interfaz de la API
    val apiService: VeterinariaApi by lazy {
        retrofit.create(VeterinariaApi::class.java)
    }
}