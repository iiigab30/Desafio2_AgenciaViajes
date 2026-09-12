package com.example.agenciaviajes

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.agenciaviajes.databinding.ActivityEditarDestinoBinding
import com.example.agenciaviajes.model.Destino
import com.example.agenciaviajes.util.ImageStorageHelper
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File

class EditarDestinoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditarDestinoBinding
    private val db = FirebaseFirestore.getInstance()
    private var imagenUri: Uri? = null
    private var rutaImagenActual = ""
    private lateinit var destinoId: String

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
        binding = ActivityEditarDestinoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        destinoId = intent.getStringExtra("destinoId") ?: run {
            finish() // si no llegó un ID válido, no tiene sentido seguir
            return
        }

        ArrayAdapter.createFromResource(
            this, R.array.lista_paises, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerPais.adapter = adapter
        }

        cargarDatosActuales()

        binding.btnSeleccionarImagen.setOnClickListener {
            seleccionarImagen.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        binding.btnGuardar.setOnClickListener {
            actualizarDestino()
        }
    }

    private fun cargarDatosActuales() {
        db.collection("destinos").document(destinoId).get()
            .addOnSuccessListener { doc ->
                val destino = doc.toObject(Destino::class.java) ?: return@addOnSuccessListener

                binding.etNombre.setText(destino.nombre)
                binding.etPrecio.setText(destino.precio.toString())
                binding.etDescripcion.setText(destino.descripcion)
                rutaImagenActual = destino.imagenPath

                // Seleccionar el país correcto en el Spinner
                val paises = resources.getStringArray(R.array.lista_paises)
                val posicion = paises.indexOf(destino.pais)
                if (posicion >= 0) binding.spinnerPais.setSelection(posicion)

                if (rutaImagenActual.isNotEmpty()) {
                    Glide.with(this).load(File(rutaImagenActual)).centerCrop().into(binding.ivPreview)
                }
            }
            .addOnFailureListener {
                mostrarError(it.localizedMessage ?: "Error al cargar el destino")
            }
    }

    private fun actualizarDestino() {
        val nombre = binding.etNombre.text.toString().trim()
        val pais = binding.spinnerPais.selectedItem?.toString() ?: ""
        val precioTexto = binding.etPrecio.text.toString().trim()
        val descripcion = binding.etDescripcion.text.toString().trim()

        if (nombre.isEmpty() || precioTexto.isEmpty() || descripcion.isEmpty()) {
            mostrarError(getString(R.string.error_campos_vacios))
            return
        }

        val precio = precioTexto.toDoubleOrNull()
        if (precio == null || precio <= 0) {
            mostrarError(getString(R.string.error_precio_invalido))
            return
        }

        if (descripcion.length < 20) {
            mostrarError(getString(R.string.error_descripcion_corta))
            return
        }

        // Si el usuario eligió una imagen nueva, la guardamos y reemplazamos la ruta.
        // Si no, seguimos usando la imagen que ya tenía (rutaImagenActual).
        val rutaFinal = if (imagenUri != null) {
            ImageStorageHelper.guardarImagen(this, imagenUri!!)
        } else {
            rutaImagenActual
        }

        if (rutaFinal.isEmpty()) {
            mostrarError(getString(R.string.error_imagen_requerida))
            return
        }

        val destinoActualizado = Destino(
            id = destinoId,
            nombre = nombre,
            pais = pais,
            precio = precio,
            descripcion = descripcion,
            imagenPath = rutaFinal
        )

        db.collection("destinos").document(destinoId).set(destinoActualizado)
            .addOnSuccessListener { finish() }
            .addOnFailureListener {
                mostrarError(it.localizedMessage ?: "Error al actualizar")
            }
    }

    private fun mostrarError(mensaje: String) {
        binding.tvError.text = mensaje
        binding.tvError.visibility = View.VISIBLE
    }
}