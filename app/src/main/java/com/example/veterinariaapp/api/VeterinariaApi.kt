package com.example.veterinariaapp.api

import com.example.veterinariaapp.models.Cita
import com.example.veterinariaapp.models.Mascota
import com.example.veterinariaapp.models.Usuario
import com.example.veterinariaapp.models.Veterinario
import retrofit2.Call
import retrofit2.http.*


interface VeterinariaApi {
    @GET("api/mascotas")
    fun obtenerMascotas(): Call<List<Mascota>>

    @GET("api/veterinarios")
    fun obtenerVeterinarios(): Call<List<Veterinario>>

    @GET("api/usuarios")
    fun obtenerUsuarios(): Call<List<Usuario>>

    @POST("api/citas")
    fun agendarCita(@Body cita: Cita): Call<Cita>

    @GET("api/citas/mias")
    fun getMisCitas(): Call<List<Cita>>

    @GET("api/citas/disponibles")
    fun obtenerHorasDisponibles(
        @Query("veterinarioId") veterinarioId: Long,
        @Query("fecha") fecha: String
    ): Call<List<String>>

    @PUT("api/citas")
    fun reprogramarCita(@Body cita: Cita): Call<Cita>

    @DELETE("api/citas")
    fun cancelarCita(@Query("id") id: Long): Call<Void>
    @GET("api/citas/veterinario")
    fun obtenerCitasPorVeterinario(@Query("veterinarioId") veterinarioId: Long): Call<List<Cita>>
    // Citas de la Agenda (con fecha opcional)
    @GET("api/citas/veterinario")
    fun obtenerCitasPorVeterinario(
        @Query("veterinarioId") veterinarioId: Long,
        @Query("fecha") fecha: String? = null // Opcional
    ): Call<List<Cita>>

    // Mis Citas (con fecha opcional)
    @GET("api/citas/mias")
    fun getMisCitas(@Query("fecha") fecha: String? = null): Call<List<Cita>>
    @GET("api/citas/mias")
    fun getMisCitas(
        @Query("fecha") fecha: String? = null,
        @Query("mascotaId") mascotaId: Long? = null // <-- NUEVO
    ): Call<List<Cita>>
}