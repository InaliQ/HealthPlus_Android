package com.softli.health.views.ui.pacientes

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.softli.health.R
import com.softli.health.views.ui.pacientes.placeholder.PlaceholderContent.PlaceholderItem
import com.softli.health.models.Paciente

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyPacienteRecyclerViewAdapter(private val pacientes: List<Paciente>) :
    RecyclerView.Adapter<MyPacienteRecyclerViewAdapter.PacienteViewHolder>() {

    class PacienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreTextView: TextView = itemView.findViewById(R.id.text_name)
        val edadTextView: TextView = itemView.findViewById(R.id.text_id)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PacienteViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.fragment_item, parent, false)
        return PacienteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PacienteViewHolder, position: Int) {
        val paciente = pacientes[position]
        holder.nombreTextView.text = paciente.nombre
        holder.edadTextView.text = paciente.numPaciente
    }

    override fun getItemCount() = pacientes.size
}