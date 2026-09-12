package com.example.agenciaviajes

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agenciaviajes.databinding.ActivityCatalogoBinding
import com.example.agenciaviajes.model.Destino
import com.google.firebase.firestore.FirebaseFirestore

class CatalogoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCatalogoBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: DestinoAdapter
    private val listaDestinos = mutableListOf<Destino>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCatalogoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = DestinoAdapter(
            listaDestinos,
            onEditar = { destino -> /* lo conectamos en la Parte 7 */ },
            onEliminar = { destino -> /* lo conectamos en la Parte 8 */ }
        )
        binding.rvDestinos.layoutManager = LinearLayoutManager(this)
        binding.rvDestinos.adapter = adapter

        binding.fabAgregar.setOnClickListener {
            startActivity(Intent(this, CrearDestinoActivity::class.java))
        }

        escucharDestinos()

    }

    private fun escucharDestinos() {
        db.collection("destinos")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                val destinos = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Destino::class.java)?.apply { id = doc.id }
                }
                adapter.actualizarLista(destinos)
            }
    }
}