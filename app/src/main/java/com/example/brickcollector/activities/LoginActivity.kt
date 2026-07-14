package com.example.brickcollector.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.brickcollector.R
import com.example.brickcollector.data.Usuario
import com.example.brickcollector.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnEntrarApp.setOnClickListener {
            val nombre = binding.tiNombre.text.toString()
            val apellidos = binding.tiApellidos.text.toString()
            val email = binding.tiEmail.text.toString()

            if ( nombre.isEmpty() || apellidos.isEmpty() || email.isEmpty() ) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val usuario = Usuario(nombre, apellidos, email)

            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("usuario", usuario)
            startActivity(intent)
            finish()
        }

        binding.btnSalirApp.setOnClickListener {
            finishAffinity()
        }
    }

}
