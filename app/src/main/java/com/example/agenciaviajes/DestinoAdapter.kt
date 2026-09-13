package com.example.agenciaviajes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.agenciaviajes.databinding.ItemDestinoBinding
import com.example.agenciaviajes.model.Destino
import java.io.File

class DestinoAdapter(
    private val destinos: MutableList<Destino>,
    private val onEditar: (Destino) -> Unit,
    private val onEliminar: (Destino) -> Unit
) : RecyclerView.Adapter<DestinoAdapter.DestinoViewHolder>() {

    inner class DestinoViewHolder(val binding: ItemDestinoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DestinoViewHolder {
        val binding = ItemDestinoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return DestinoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DestinoViewHolder, position: Int) {
        val destino = destinos[position]
        with(holder.binding) {
            tvNombre.text = destino.nombre
            tvPrecio.text = "$${"%.2f".format(destino.precio)}"
            tvDescripcion.text = destino.descripcion

            if (destino.imagenPath.isNotEmpty()) {
                Glide.with(root.context)
                    .load(File(destino.imagenPath))
                    .centerCrop()
                    .into(ivDestino)
            }

            btnEditar.setOnClickListener { onEditar(destino) }
            btnEliminar.setOnClickListener { onEliminar(destino) }
        }
    }

    override fun getItemCount() = destinos.size

    fun actualizarLista(nuevaLista: List<Destino>) {
        destinos.clear()
        destinos.addAll(nuevaLista)
        notifyDataSetChanged()
    }
}