package com.example.brickcollector.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.brickcollector.data.Usuario
import com.example.brickcollector.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    companion object {
        private const val PREFS_NAME = "BrickCollectorPrefs"
        private const val KEY_NOMBRE = "nombre"
        private const val KEY_APELLIDOS = "apellidos"
        private const val KEY_EMAIL = "email"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Auto-login: si ya hay un usuario guardado, saltar directamente
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val nombreGuardado = prefs.getString(KEY_NOMBRE, null)
        if (!nombreGuardado.isNullOrBlank()) {
            val usuario = Usuario(
                nombre = nombreGuardado,
                apellidos = prefs.getString(KEY_APELLIDOS, "") ?: "",
                email = prefs.getString(KEY_EMAIL, "") ?: ""
            )
            lanzarMainActivity(usuario)
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnEntrarApp.setOnClickListener {
            val nombre = binding.tiNombre.text.toString().trim()
            val apellidos = binding.tiApellidos.text.toString().trim()
            val email = binding.tiEmail.text.toString().trim()

            if (nombre.isEmpty() || apellidos.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Guardar usuario en SharedPreferences para futuros accesos
            prefs.edit()
                .putString(KEY_NOMBRE, nombre)
                .putString(KEY_APELLIDOS, apellidos)
                .putString(KEY_EMAIL, email)
                .apply()

            val usuario = Usuario(nombre, apellidos, email)
            lanzarMainActivity(usuario)
        }

        binding.btnSalirApp.setOnClickListener {
            finishAffinity()
        }
    }

    private fun lanzarMainActivity(usuario: Usuario) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("usuario", usuario)
        startActivity(intent)
        finish()
    }
}
