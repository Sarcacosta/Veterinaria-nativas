package com.example.veterinariaapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.veterinariaapp.api.ApiClient
import com.example.veterinariaapp.models.Cita
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MisCitasViewModel : ViewModel() {
    val citas = MutableLiveData<List<Cita>>()
    val isLoading = MutableLiveData<Boolean>()
    val error = MutableLiveData<String?>()

    fun cargarMisCitas(fecha: String? = null, mascotaId: Long? = null) {
        isLoading.value = true
        error.value = null

        ApiClient.apiService.getMisCitas(fecha, mascotaId).enqueue(object : Callback<List<Cita>> {


            override fun onResponse(call: Call<List<Cita>>, response: Response<List<Cita>>) {
                isLoading.value = false
                if (response.isSuccessful) {
                    citas.value = response.body() ?: emptyList()
                } else {
                    error.value = "Error al cargar citas: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<List<Cita>>, t: Throwable) {
                isLoading.value = false
                error.value = "Error de conexión: ${t.message}"
            }
        })
    }
}