package com.example.veterinariaapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.veterinariaapp.api.ApiClient
import com.example.veterinariaapp.models.Veterinario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VeterinariosViewModel : ViewModel() {
    val veterinarios = MutableLiveData<List<Veterinario>>()
    val isLoading = MutableLiveData<Boolean>()
    val error = MutableLiveData<String?>()

    fun cargarVeterinarios() {
        isLoading.value = true
        error.value = null

        ApiClient.apiService.obtenerVeterinarios().enqueue(object : Callback<List<Veterinario>> {
            override fun onResponse(call: Call<List<Veterinario>>, response: Response<List<Veterinario>>) {
                isLoading.value = false
                if (response.isSuccessful) {
                    veterinarios.value = response.body() ?: emptyList()
                } else {
                    error.value = "Error al cargar: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<List<Veterinario>>, t: Throwable) {
                isLoading.value = false
                error.value = "Error de conexión: ${t.message}"
            }
        })
    }
}