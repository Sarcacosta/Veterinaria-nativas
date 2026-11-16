package com.example.veterinariaapp.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.veterinariaapp.R
import com.example.veterinariaapp.api.ApiClient
import com.example.veterinariaapp.models.Veterinario
import com.example.veterinariaapp.viewmodels.SolicitarCitaViewModel
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SolicitarCitaActivity : AppCompatActivity() {

    private val viewModel: SolicitarCitaViewModel by viewModels()
    private lateinit var etFecha: TextInputEditText
    private lateinit var spinnerVeterinarios: Spinner
    private lateinit var spinnerHoras: Spinner // <-- NUEVO
    private var listaVeterinarios: List<Veterinario> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_solicitar_cita)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Solicitar Cita"

        val etMascotaId = findViewById<TextInputEditText>(R.id.etMascotaId)
        etFecha = findViewById(R.id.etFecha)
        spinnerVeterinarios = findViewById(R.id.spinnerVeterinarios)
        spinnerHoras = findViewById(R.id.spinnerHoras) // <-- NUEVO
        val btnAgendar = findViewById<Button>(R.id.btnAgendar)
        val tvError = findViewById<TextView>(R.id.tvErrorForm)

        // 1. Cargar veterinarios
        cargarVeterinariosEnSpinner()

        // 2. Seleccionar Fecha -> Cargar Horas Disponibles
        etFecha.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(this, { _, year, month, dayOfMonth ->
                val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                calendar.set(year, month, dayOfMonth)
                val fechaSeleccionada = formato.format(calendar.time)
                etFecha.setText(fechaSeleccionada)

                // Al elegir la fecha, cargamos las horas del veterinario seleccionado
                val vetId = obtenerVeterinarioIdSeleccionado()
                if (vetId != 0L) {
                    cargarHorasDisponibles(vetId, fechaSeleccionada)
                } else {
                    Toast.makeText(this, "Selecciona un veterinario primero", Toast.LENGTH_SHORT).show()
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            datePicker.datePicker.minDate = System.currentTimeMillis()
            datePicker.show()
        }

        // 3. Botón Agendar
        btnAgendar.setOnClickListener {
            val mascotaId = etMascotaId.text.toString().toLongOrNull() ?: 0L
            val veterinarioId = obtenerVeterinarioIdSeleccionado()
            val fecha = etFecha.text.toString()
            val hora = spinnerHoras.selectedItem?.toString() ?: ""

            // Validar que no sea la opción por defecto del spinner
            if (hora == "Sin horarios disponibles" || hora.isEmpty()) {
                Toast.makeText(this, "Selecciona una hora válida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.agendarCita(mascotaId, veterinarioId, fecha, hora)
        }

        // 4. Observadores del ViewModel
        viewModel.error.observe(this) { error ->
            tvError.text = error
            tvError.visibility = if (error != null) View.VISIBLE else View.GONE
        }

        viewModel.exito.observe(this) { exito ->
            if (exito) {
                Toast.makeText(this, "Cita agendada con éxito", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    // Función auxiliar para obtener el ID del veterinario seleccionado
    private fun obtenerVeterinarioIdSeleccionado(): Long {
        return if (spinnerVeterinarios.selectedItemPosition >= 0 && listaVeterinarios.isNotEmpty()) {
            listaVeterinarios[spinnerVeterinarios.selectedItemPosition].id ?: 0L
        } else {
            0L
        }
    }

    // --- CARGAR VETERINARIOS EN EL SPINNER ---
    private fun cargarVeterinariosEnSpinner() {
        ApiClient.apiService.obtenerVeterinarios().enqueue(object : Callback<List<Veterinario>> {
            override fun onResponse(call: Call<List<Veterinario>>, response: Response<List<Veterinario>>) {
                if (response.isSuccessful) {
                    listaVeterinarios = response.body() ?: emptyList()
                    val nombres = listaVeterinarios.map { "${it.nombres} ${it.apellidos} - ${it.especialidad}" }
                    val adapter = ArrayAdapter(this@SolicitarCitaActivity, android.R.layout.simple_spinner_item, nombres)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerVeterinarios.adapter = adapter
                }
            }

            override fun onFailure(call: Call<List<Veterinario>>, t: Throwable) {
                Toast.makeText(this@SolicitarCitaActivity, "Error al cargar vets", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // --- NUEVA FUNCIÓN: CARGAR HORAS DISPONIBLES ---
    private fun cargarHorasDisponibles(veterinarioId: Long, fecha: String) {
        // Mostramos un mensaje temporal en el spinner mientras carga
        val opcionesCargando = listOf("Cargando horarios...")
        val adapterCargando = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesCargando)
        spinnerHoras.adapter = adapterCargando

        ApiClient.apiService.obtenerHorasDisponibles(veterinarioId, fecha).enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                if (response.isSuccessful) {
                    val horas = response.body() ?: emptyList()
                    val opciones = if (horas.isEmpty()) listOf("Sin horarios disponibles") else horas
                    val adapter = ArrayAdapter(this@SolicitarCitaActivity, android.R.layout.simple_spinner_item, opciones)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerHoras.adapter = adapter
                } else {
                    Toast.makeText(this@SolicitarCitaActivity, "Error al cargar horarios (${response.code()})", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                Toast.makeText(this@SolicitarCitaActivity, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}