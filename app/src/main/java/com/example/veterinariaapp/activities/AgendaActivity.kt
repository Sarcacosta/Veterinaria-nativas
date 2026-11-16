package com.example.veterinariaapp.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.veterinariaapp.R
import com.example.veterinariaapp.api.ApiClient
import com.example.veterinariaapp.models.Cita
import com.example.veterinariaapp.models.Veterinario
import com.example.veterinariaapp.viewmodels.AgendaViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar
import java.util.Locale

class AgendaActivity : AppCompatActivity() {

    private val viewModel: AgendaViewModel by viewModels()
    private lateinit var rvAgenda: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEstadoVacio: TextView
    private lateinit var spinnerVets: Spinner
    private lateinit var btnFiltrarFecha: Button
    private var listaVeterinarios: List<Veterinario> = emptyList()
    private var fechaFiltro: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agenda)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Agenda por Veterinario"

        rvAgenda = findViewById(R.id.rvAgenda)
        progressBar = findViewById(R.id.progressBarAgenda)
        tvEstadoVacio = findViewById(R.id.tvEstadoVacioAgenda)
        spinnerVets = findViewById(R.id.spinnerVetsAgenda)
        btnFiltrarFecha = findViewById(R.id.btnFiltrarFechaAgenda)

        rvAgenda.layoutManager = LinearLayoutManager(this)

        cargarVeterinarios()

        spinnerVets.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (listaVeterinarios.isNotEmpty()) {
                    val vetId = listaVeterinarios[position].id ?: 0L
                    viewModel.cargarAgenda(vetId, fechaFiltro)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnFiltrarFecha.setOnClickListener {
            val cal = Calendar.getInstance()
            val picker = DatePickerDialog(this, { _, y, m, d ->
                fechaFiltro = String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, d)
                val vetId = listaVeterinarios[spinnerVets.selectedItemPosition].id ?: 0L
                viewModel.cargarAgenda(vetId, fechaFiltro)
                Toast.makeText(this, "Filtrando por: $fechaFiltro", Toast.LENGTH_SHORT).show()
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
            picker.show()
        }

        viewModel.citas.observe(this) { lista ->
            if (lista.isNullOrEmpty()) {
                tvEstadoVacio.visibility = View.VISIBLE
                rvAgenda.visibility = View.GONE
            } else {
                tvEstadoVacio.visibility = View.GONE
                rvAgenda.visibility = View.VISIBLE
                rvAgenda.adapter = AgendaAdapter(lista) { cita ->
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
    }

    private fun cargarVeterinarios() {
        ApiClient.apiService.obtenerVeterinarios().enqueue(object : Callback<List<Veterinario>> {
            override fun onResponse(call: Call<List<Veterinario>>, response: Response<List<Veterinario>>) {
                if (response.isSuccessful) {
                    listaVeterinarios = response.body() ?: emptyList()
                    val nombres = listaVeterinarios.map { "${it.nombres} ${it.apellidos}" }
                    val adapter = ArrayAdapter(this@AgendaActivity, android.R.layout.simple_spinner_item, nombres)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerVets.adapter = adapter
                }
            }
            override fun onFailure(call: Call<List<Veterinario>>, t: Throwable) {
                Toast.makeText(this@AgendaActivity, "Error al cargar vets", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (listaVeterinarios.isNotEmpty()) {
            val vetId = listaVeterinarios[spinnerVets.selectedItemPosition].id ?: 0L
            viewModel.cargarAgenda(vetId, fechaFiltro)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}

class AgendaAdapter(
    private val lista: List<Cita>,
    private val onClick: (Cita) -> Unit
) : RecyclerView.Adapter<AgendaAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView: CardView = view as CardView
        val tvMascotaId: TextView = view.findViewById(R.id.tvMascotaId)
        val tvInfo: TextView = view.findViewById(R.id.tvFechaHora)
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
        holder.tvInfo.text = "${cita.fecha} - ${cita.hora}"
        holder.tvEstado.text = "Estado: ${cita.estado}"

        when (cita.estado?.uppercase()) {
            "CANCELADA" -> {
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.estado_cancelada))
                holder.tvEstado.setTextColor(ContextCompat.getColor(context, R.color.texto_cancelada))
                holder.tvInfo.setTextColor(ContextCompat.getColor(context, R.color.ciruela_oscura))
                holder.tvMascotaId.setTextColor(ContextCompat.getColor(context, R.color.texto_cancelada))
            }
            "REPROGRAMADA" -> {
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.estado_reprogramada))
                holder.tvEstado.setTextColor(ContextCompat.getColor(context, R.color.texto_reprogramada))
                holder.tvInfo.setTextColor(ContextCompat.getColor(context, R.color.ciruela_oscura))
                holder.tvMascotaId.setTextColor(ContextCompat.getColor(context, R.color.ciruela_oscura))
            }
            "PENDIENTE" -> {
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.estado_pendiente))
                holder.tvEstado.setTextColor(ContextCompat.getColor(context, R.color.texto_pendiente))
                holder.tvInfo.setTextColor(ContextCompat.getColor(context, R.color.ciruela_oscura))
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