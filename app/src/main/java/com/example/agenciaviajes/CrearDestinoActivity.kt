package com.example.agenciaviajes

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.appcompat.app.AppCompatActivity
import com.example.agenciaviajes.databinding.ActivityCrearDestinoBinding
import com.example.agenciaviajes.model.Destino
import com.example.agenciaviajes.util.ImageStorageHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CrearDestinoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrearDestinoBinding
    private val db = FirebaseFirestore.getInstance()
    private var imagenUri: Uri? = null

    private val seleccionarImagen = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            imagenUri = uri
            binding.ivPreview.setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearDestinoBinding.inflate(layoutInflater)
        setContentView(binding.root)


        ArrayAdapter.createFromResource(
            this, R.array.lista_paises, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerPais.adapter = adapter
        }

        binding.btnSeleccionarImagen.setOnClickListener {
            seleccionarImagen.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        binding.btnGuardar.setOnClickListener {
            guardarDestino()
        }
    }

    private fun guardarDestino() {
        val nombre = binding.etNombre.text.toString().trim()
        val pais = binding.spinnerPais.selectedItem?.toString() ?: ""
        val precioTexto = binding.etPrecio.text.toString().trim()
        val descripcion = binding.etDescripcion.text.toString().trim()

        // validacion 1 de los campos vacíos
        if (nombre.isEmpty() || precioTexto.isEmpty() || descripcion.isEmpty()) {
            mostrarError(getString(R.string.error_campos_vacios))
            return
        }

        // validacion 2 sobre el precio numerico y mayor a 0
        val precio = precioTexto.toDoubleOrNull()
        if (precio == null || precio <= 0) {
            mostrarError(getString(R.string.error_precio_invalido))
            return
        }

        // validacion 3 sobre la descripción mínima
        if (descripcion.length < 20) {
            mostrarError(getString(R.string.error_descripcion_corta))
            return
        }

        // validacion 4 la imagen obligatoria
        if (imagenUri == null) {
            mostrarError(getString(R.string.error_imagen_requerida))
            return
        }

        val rutaImagen = ImageStorageHelper.guardarImagen(this, imagenUri!!)

        val destino = Destino(
            nombre = nombre,
            pais = pais,
            precio = precio,
            descripcion = descripcion,
            imagenPath = rutaImagen,
            userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        )

        db.collection("destinos").add(destino)
            .addOnSuccessListener { finish() }
            .addOnFailureListener { error ->
                mostrarError(error.localizedMessage ?: "Error al guardar")
            }
    }

    private fun mostrarError(mensaje: String) {
        binding.tvError.text = mensaje
        binding.tvError.visibility = View.VISIBLE
    }
}