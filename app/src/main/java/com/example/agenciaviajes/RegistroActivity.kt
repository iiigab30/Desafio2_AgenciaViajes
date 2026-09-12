package com.example.agenciaviajes

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.agenciaviajes.databinding.ActivityRegistroBinding
import com.google.firebase.auth.FirebaseAuth

class RegistroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnRegistrar.setOnClickListener {
            intentarRegistro()
        }

        binding.tvIrLogin.setOnClickListener {
            finish() // regresa a LoginActivity, que ya estaba abierta debajo
        }
    }

    private fun intentarRegistro() {
        val correo = binding.etCorreo.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (correo.isEmpty() || password.isEmpty()) {
            mostrarError(getString(R.string.error_campos_vacios))
            return
        }

        if (password.length < 6) {
            mostrarError(getString(R.string.error_password_corta))
            return
        }

        auth.createUserWithEmailAndPassword(correo, password)
            .addOnSuccessListener {
                startActivity(Intent(this, CatalogoActivity::class.java))
                finish()
            }
            .addOnFailureListener { error ->
                mostrarError(error.localizedMessage ?: "Error al registrar usuario")
            }
    }

    private fun mostrarError(mensaje: String) {
        binding.tvError.text = mensaje
        binding.tvError.visibility = View.VISIBLE
    }
}