package com.example.veterinariaapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.veterinariaapp.api.ApiClient
import com.example.veterinariaapp.models.Cita
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SolicitarCitaViewModel : ViewModel() {

    val isLoading = MutableLiveData<Boolean>()
    val error = MutableLiveData<String?>()
    val exito = MutableLiveData<Boolean>()

    fun agendarCita(mascotaId: Long, veterinarioId: Long, fecha: String, hora: String) {
        // Validación de campos vacíos en el cliente
        if (mascotaId == 0L || veterinarioId == 0L || fecha.isBlank() || hora.isBlank()) {
            error.value = "Todos los campos son obligatorios"
            return
        }

        isLoading.value = true
        error.value = null

        val nuevaCita = Cita(
            id = null,
            mascotaId = mascotaId,
            veterinarioId = veterinarioId,
            fecha = fecha,
            hora = hora,
            motivo = "Consulta",
            estado = "PENDIENTE"
        )

        ApiClient.apiService.agendarCita(nuevaCita).enqueue(object : Callback<Cita> {
            override fun onResponse(call: Call<Cita>, response: Response<Cita>) {
                isLoading.value = false

                if (response.isSuccessful) {
                    exito.value = true
                } else {
                    // Si el backend responde con error (400, 409, 500)
                    if (response.code() == 409) {
                        val errorBody = response.errorBody()?.string()

                        // Evaluamos el mensaje que envía el backend
                        if (errorBody?.contains("DOBLE_RESERVA") == true) {
                            error.value = "El veterinario ya tiene una cita agendada en esa fecha y hora. Por favor, elige otro horario."
                        } else if (errorBody?.contains("MASCOTA_CON_CITA") == true) {
                            error.value = "Esta mascota ya tiene una cita activa. Debes cancelarla antes de agendar una nueva."
                        } else {
                            error.value = "Conflicto: El horario o la mascota ya están ocupados."
                        }
                    } else {
                        error.value = "Error ${response.code()}: ${response.message()}"
                    }
                }
            }

            override fun onFailure(call: Call<Cita>, t: Throwable) {
                isLoading.value = false
                error.value = "Error de conexión: ${t.message}"
            }
        })
    }
}
