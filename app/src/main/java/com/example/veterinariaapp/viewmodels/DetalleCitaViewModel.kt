package com.example.veterinariaapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.veterinariaapp.api.ApiClient
import com.example.veterinariaapp.models.Cita
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetalleCitaViewModel : ViewModel() {

    val horasDisponibles = MutableLiveData<List<String>>()
    val operacionExitosa = MutableLiveData<String?>()
    val error = MutableLiveData<String?>()
    val isLoading = MutableLiveData<Boolean>()

    fun cargarHorasDisponibles(veterinarioId: Long, fecha: String) {
        isLoading.value = true
        ApiClient.apiService.obtenerHorasDisponibles(veterinarioId, fecha)
            .enqueue(object : Callback<List<String>> {
                override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                    isLoading.value = false
                    if (response.isSuccessful) {
                        horasDisponibles.value = response.body() ?: emptyList()
                    } else {
                        error.value = "Error al cargar horarios (${response.code()})"
                    }
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    isLoading.value = false
                    error.value = "Error de red: ${t.message}"
                }
            })
    }

    fun reprogramarCita(citaModificada: Cita) {
        isLoading.value = true
        ApiClient.apiService.reprogramarCita(citaModificada).enqueue(object : Callback<Cita> {
            override fun onResponse(call: Call<Cita>, response: Response<Cita>) {
                isLoading.value = false
                if (response.isSuccessful) {
                    operacionExitosa.value = "Cita reprogramada con éxito"
                } else {
                    error.value = "Error al modificar: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<Cita>, t: Throwable) {
                isLoading.value = false
                error.value = "Error de conexión: ${t.message}"
            }
        })
    }

    fun cancelarCita(id: Long) {
        isLoading.value = true
        ApiClient.apiService.cancelarCita(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                isLoading.value = false
                if (response.isSuccessful) {
                    operacionExitosa.value = "Cita cancelada correctamente"
                } else {
                    error.value = "Error al cancelar: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                isLoading.value = false
                error.value = "Error de conexión: ${t.message}"
            }
        })
    }
}