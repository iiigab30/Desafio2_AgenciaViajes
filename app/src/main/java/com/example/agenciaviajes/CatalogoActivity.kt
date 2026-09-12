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
            onEditar = { destino -> abrirEditar(destino) },
            onEliminar = { destino -> confirmarEliminar(destino) },
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

    private fun abrirEditar(destino: Destino) {
        val intent = Intent(this, EditarDestinoActivity::class.java)
        intent.putExtra("destinoId", destino.id)
        startActivity(intent)
    }

    private fun confirmarEliminar(destino: Destino) {
        android.app.AlertDialog.Builder(this)
            .setTitle(getString(R.string.confirmar_eliminar_titulo))
            .setMessage(getString(R.string.confirmar_eliminar_mensaje))
            .setPositiveButton(getString(R.string.si)) { _, _ ->
                db.collection("destinos").document(destino.id).delete()
            }
            .setNegativeButton(getString(R.string.cancelar), null)
            .show()
    }
}