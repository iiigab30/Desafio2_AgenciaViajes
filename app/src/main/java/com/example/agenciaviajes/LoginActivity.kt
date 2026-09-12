package com.example.agenciaviajes

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.agenciaviajes.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import kotlin.jvm.java

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnLogin.setOnClickListener {
            intentarLogin()
        }

        binding.tvIrRegistro.setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }
    }

    private fun intentarLogin() {
        val correo = binding.etCorreo.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (correo.isEmpty() || password.isEmpty()) {
            mostrarError(getString(R.string.error_campos_vacios))
            return
        }

        auth.signInWithEmailAndPassword(correo, password)
            .addOnSuccessListener {
                // Login exitoso — por ahora solo cerramos esta pantalla.
                // En la Parte 4 la conectamos con el catálogo.
                finish()
            }
            .addOnFailureListener { error ->
                mostrarError(error.localizedMessage ?: "Error al iniciar sesión")
            }
    }

    private fun mostrarError(mensaje: String) {
        binding.tvError.text = mensaje
        binding.tvError.visibility = View.VISIBLE
    }
}