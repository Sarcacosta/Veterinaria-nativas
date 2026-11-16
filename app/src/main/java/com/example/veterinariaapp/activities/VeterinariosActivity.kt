package com.example.veterinariaapp.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.veterinariaapp.R
import com.example.veterinariaapp.models.Veterinario
import com.example.veterinariaapp.viewmodels.VeterinariosViewModel

class VeterinariosActivity : AppCompatActivity() {

    private val viewModel: VeterinariosViewModel by viewModels()
    private lateinit var rvVeterinarios: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvError: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_veterinarios)

        // Barra superior
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Veterinarios"

        rvVeterinarios = findViewById(R.id.rvVeterinarios)
        progressBar = findViewById(R.id.progressBar)
        tvError = findViewById(R.id.tvError)

        rvVeterinarios.layoutManager = LinearLayoutManager(this)

        // Observadores
        viewModel.veterinarios.observe(this) { lista ->
            rvVeterinarios.adapter = VeterinarioAdapter(lista)
        }
        viewModel.isLoading.observe(this) { loading ->
            progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }
        viewModel.error.observe(this) { error ->
            tvError.text = error
            tvError.visibility = if (error != null) View.VISIBLE else View.GONE
        }

        // Llamar a la API
        viewModel.cargarVeterinarios()
    }

    //  FUNCIÓN DE LA FLECHA
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }


} //  Aquí termina la clase VeterinariosActivity


class VeterinarioAdapter(private val lista: List<Veterinario>) : RecyclerView.Adapter<VeterinarioAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombreVet)
        val tvEspecialidad: TextView = view.findViewById(R.id.tvEspecialidad)
        val tvEstado: TextView = view.findViewById(R.id.tvEstado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_veterinario, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vet = lista[position]
        holder.tvNombre.text = "Dr(a). ${vet.nombres} ${vet.apellidos}"
        holder.tvEspecialidad.text = "Especialidad: ${vet.especialidad}"
        holder.tvEstado.text = "Estado: ${vet.estado}"
    }

    override fun getItemCount() = lista.size
}