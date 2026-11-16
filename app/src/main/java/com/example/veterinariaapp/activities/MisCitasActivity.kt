package com.example.veterinariaapp.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.veterinariaapp.R
import com.example.veterinariaapp.models.Cita
import com.example.veterinariaapp.viewmodels.MisCitasViewModel
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar
import java.util.Locale

class MisCitasActivity : AppCompatActivity() {

    private val viewModel: MisCitasViewModel by viewModels()
    private lateinit var rvCitas: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEstadoVacio: TextView
    private lateinit var etFiltroMascota: TextInputEditText

    private var fechaFiltro: String? = null
    private var mascotaFiltro: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_citas)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Mis Citas"

        rvCitas = findViewById(R.id.rvCitas)
        progressBar = findViewById(R.id.progressBarCitas)
        tvEstadoVacio = findViewById(R.id.tvEstadoVacioCitas)
        etFiltroMascota = findViewById(R.id.etFiltroMascota)
        val btnFiltrarFecha = findViewById<Button>(R.id.btnFiltrarFecha)
        val btnBuscarMascota = findViewById<Button>(R.id.btnBuscarMascota)

        rvCitas.layoutManager = LinearLayoutManager(this)

        // Filtro por fecha
        btnFiltrarFecha.setOnClickListener {
            val cal = Calendar.getInstance()
            val picker = DatePickerDialog(this, { _, y, m, d ->
                fechaFiltro = String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, d)
                viewModel.cargarMisCitas(fechaFiltro, mascotaFiltro)
                Toast.makeText(this, "Filtrando por: $fechaFiltro", Toast.LENGTH_SHORT).show()
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
            picker.show()
        }

        // Filtro por ID de mascota
        btnBuscarMascota.setOnClickListener {
            val idTexto = etFiltroMascota.text.toString()
            mascotaFiltro = if (idTexto.isNotEmpty()) idTexto.toLongOrNull() else null
            viewModel.cargarMisCitas(fechaFiltro, mascotaFiltro)
            if (mascotaFiltro != null) {
                Toast.makeText(this, "Filtrando mascota ID: $mascotaFiltro", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.citas.observe(this) { lista ->
            if (lista.isNullOrEmpty()) {
                tvEstadoVacio.text = "No hay citas que coincidan con los filtros."
                tvEstadoVacio.visibility = View.VISIBLE
                rvCitas.visibility = View.GONE
            } else {
                tvEstadoVacio.visibility = View.GONE
                rvCitas.visibility = View.VISIBLE
                rvCitas.adapter = CitaAdapter(lista) { cita ->
                    val intent = Intent(this, DetalleCitaActivity::class.java).apply {
                        putExtra("cita_id", cita.id)
                        putExtra("mascota_id", cita.mascotaId)
                        putExtra("veterinario_id", cita.veterinarioId)
                        putExtra("fecha", cita.fecha)
                        putExtra("hora", cita.hora)
                        putExtra("estado", cita.estado)
                    }
                    startActivity(intent)
                }
            }
        }

        viewModel.isLoading.observe(this) { loading ->
            progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { mensajeError ->
            if (mensajeError != null) {
                tvEstadoVacio.text = mensajeError
                tvEstadoVacio.visibility = View.VISIBLE
                rvCitas.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.cargarMisCitas(fechaFiltro, mascotaFiltro)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}

// Adaptador con ID de mascota y colores
class CitaAdapter(
    private val lista: List<Cita>,
    private val onClick: (Cita) -> Unit
) : RecyclerView.Adapter<CitaAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView: CardView = view as CardView
        val tvMascotaId: TextView = view.findViewById(R.id.tvMascotaId)
        val tvFechaHora: TextView = view.findViewById(R.id.tvFechaHora)
        val tvEstado: TextView = view.findViewById(R.id.tvEstadoCita)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cita, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cita = lista[position]
        val context = holder.itemView.context

        holder.tvMascotaId.text = "Mascota ID: ${cita.mascotaId}"
        holder.tvFechaHora.text = "Fecha: ${cita.fecha} - ${cita.hora}"
        holder.tvEstado.text = "Estado: ${cita.estado}"

        when (cita.estado?.uppercase()) {
            "CANCELADA" -> {
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.estado_cancelada))
                holder.tvEstado.setTextColor(ContextCompat.getColor(context, R.color.texto_cancelada))
                holder.tvFechaHora.setTextColor(ContextCompat.getColor(context, R.color.texto_cancelada))
                holder.tvMascotaId.setTextColor(ContextCompat.getColor(context, R.color.texto_cancelada))
            }
            "REPROGRAMADA" -> {
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.estado_reprogramada))
                holder.tvEstado.setTextColor(ContextCompat.getColor(context, R.color.texto_reprogramada))
                holder.tvFechaHora.setTextColor(ContextCompat.getColor(context, R.color.ciruela_oscura))
                holder.tvMascotaId.setTextColor(ContextCompat.getColor(context, R.color.ciruela_oscura))
            }
            "PENDIENTE" -> {
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.estado_pendiente))
                holder.tvEstado.setTextColor(ContextCompat.getColor(context, R.color.texto_pendiente))
                holder.tvFechaHora.setTextColor(ContextCompat.getColor(context, R.color.ciruela_oscura))
                holder.tvMascotaId.setTextColor(ContextCompat.getColor(context, R.color.ciruela_oscura))
            }
            else -> {
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.lavanda_suave))
                holder.tvEstado.setTextColor(ContextCompat.getColor(context, R.color.violeta_medio))
                holder.tvMascotaId.setTextColor(ContextCompat.getColor(context, R.color.gris_violaceo))
            }
        }
        holder.itemView.setOnClickListener { onClick(cita) }
    }

    override fun getItemCount() = lista.size
}