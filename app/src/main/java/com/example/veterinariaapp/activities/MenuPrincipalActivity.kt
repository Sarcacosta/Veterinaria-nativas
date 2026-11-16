package com.example.veterinariaapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.veterinariaapp.R

class MenuPrincipalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_principal)

        findViewById<Button>(R.id.btnVeterinarios).setOnClickListener {
            startActivity(Intent(this, VeterinariosActivity::class.java))
        }
        findViewById<Button>(R.id.btnMisCitas).setOnClickListener {
            startActivity(Intent(this, MisCitasActivity::class.java))
        }
        findViewById<Button>(R.id.btnSolicitarCita).setOnClickListener {
            startActivity(Intent(this, SolicitarCitaActivity::class.java))

        }
        findViewById<Button>(R.id.btnAgenda).setOnClickListener {
            startActivity(Intent(this, AgendaActivity::class.java))
        }
    }
}