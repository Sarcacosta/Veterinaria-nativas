package com.example.veterinariaapp.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.veterinariaapp.R
import com.example.veterinariaapp.models.Cita
import com.example.veterinariaapp.viewmodels.DetalleCitaViewModel
import java.util.Calendar
import java.util.Locale

class DetalleCitaActivity : AppCompatActivity() {

    private val viewModel: DetalleCitaViewModel by viewModels()

    private var mascotaIdActual: Long = 1L
    private var veterinarioIdActual: Long = 1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_cita)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detalle de Cita"

        val citaId = intent.getLongExtra("cita_id", 0L)
        val fechaIntent = intent.getStringExtra("fecha") ?: ""
        val horaIntent = intent.getStringExtra("hora") ?: ""
        val estadoIntent = intent.getStringExtra("estado") ?: "PENDIENTE"
        mascotaIdActual = intent.getLongExtra("mascota_id", 1L)
        veterinarioIdActual = intent.getLongExtra("veterinario_id", 1L)

        val tvIdCita = findViewById<TextView>(R.id.tvIdCita)
        val tvFecha = findViewById<TextView>(R.id.tvFechaDetalle)
        val tvHora = findViewById<TextView>(R.id.tvHoraDetalle)
        val tvEstado = findViewById<TextView>(R.id.tvEstadoDetalle)

        val etNuevaFecha = findViewById<EditText>(R.id.etNuevaFecha)
        val spHoras = findViewById<Spinner>(R.id.spHorasDisponiblesDetalle)
        val pb = findViewById<ProgressBar>(R.id.pbDetalleCita)

        val btnModificar = findViewById<Button>(R.id.btnModificarCita)
        val btnCancelar = findViewById<Button>(R.id.btnCancelarCita)
        val btnVolver = findViewById<Button>(R.id.btnVolver)

        btnVolver.setOnClickListener { finish() }

        // Llenar datos iniciales
        tvIdCita.text = "ID Cita: #$citaId"
        tvFecha.text = "Fecha: $fechaIntent"
        tvHora.text = "Hora: $horaIntent"
        tvEstado.text = "Estado: $estadoIntent"
        etNuevaFecha.setText(fechaIntent)

        if (fechaIntent.isNotEmpty()) {
            viewModel.cargarHorasDisponibles(veterinarioIdActual, fechaIntent)
        }

        // Seleccionar nueva fecha
        etNuevaFecha.setOnClickListener {
            val cal = Calendar.getInstance()
            val picker = DatePickerDialog(this, { _, y, m, d ->
                val fechaSel = String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, d)
                etNuevaFecha.setText(fechaSel)
                viewModel.cargarHorasDisponibles(veterinarioIdActual, fechaSel)
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
            picker.datePicker.minDate = System.currentTimeMillis()
            picker.show()
        }

        // Observadores
        viewModel.horasDisponibles.observe(this) { listaHoras ->
            val opciones = if (listaHoras.isNullOrEmpty()) listOf("Sin horarios disponibles") else listaHoras
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opciones)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spHoras.adapter = adapter
        }

        viewModel.isLoading.observe(this) { loading ->
            pb.visibility = if (loading == true) View.VISIBLE else View.GONE
        }

        viewModel.operacionExitosa.observe(this) { msg ->
            if (!msg.isNullOrEmpty()) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        viewModel.error.observe(this) { err ->
            if (!err.isNullOrEmpty()) {
                Toast.makeText(this, err, Toast.LENGTH_LONG).show()
            }
        }

        // Modificar
        btnModificar.setOnClickListener {
            val nuevaFecha = etNuevaFecha.text.toString()
            val nuevaHora = spHoras.selectedItem?.toString() ?: ""

            if (nuevaFecha.isEmpty() || nuevaHora.isEmpty() || nuevaHora == "Sin horarios disponibles") {
                Toast.makeText(this, "Selecciona una fecha y hora válida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val citaModificada = Cita(
                id = citaId,
                mascotaId = mascotaIdActual,
                veterinarioId = veterinarioIdActual,
                fecha = nuevaFecha,
                hora = nuevaHora,
                estado = "REPROGRAMADA"
            )
            viewModel.reprogramarCita(citaModificada)
        }

        // Cancelar
        btnCancelar.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Cancelar Cita")
                .setMessage("¿Estás segura de que deseas cancelar esta cita?")
                .setPositiveButton("Sí, cancelar") { _, _ ->
                    viewModel.cancelarCita(citaId)
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}