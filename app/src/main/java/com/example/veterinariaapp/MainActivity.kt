package com.example.veterinariaapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.veterinariaapp.activities.MenuPrincipalActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Redirigimos al menú principal
        startActivity(Intent(this, MenuPrincipalActivity::class.java))
        finish() // Cerramos MainActivity para que no quede en la pila
    }
}