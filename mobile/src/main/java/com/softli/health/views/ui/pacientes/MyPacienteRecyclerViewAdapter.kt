package com.softli.health.views.ui.pacientes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.softli.health.R
import com.softli.health.models.Paciente

class MyPacienteRecyclerViewAdapter(
    private val pacientes: List<Paciente>,
    private val onWarningClick: (Paciente) -> Unit,
    private val onRecordatorioClick: (Paciente) -> Unit
) : RecyclerView.Adapter<MyPacienteRecyclerViewAdapter.PacienteViewHolder>() {

    class PacienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreTextView: TextView = itemView.findViewById(R.id.text_name)
        val edadTextView: TextView = itemView.findViewById(R.id.text_id)
        val warningButton: ImageButton = itemView.findViewById(R.id.icon_warning)
        val recordatorioButton: ImageButton = itemView.findViewById(R.id.icon_recordatorio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PacienteViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.fragment_item, parent, false)
        return PacienteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PacienteViewHolder, position: Int) {
        val paciente = pacientes[position]
        holder.nombreTextView.text = paciente.nombre
        holder.edadTextView.text = paciente.numPaciente

        holder.warningButton.setOnClickListener {
            onWarningClick(paciente)
        }

        holder.recordatorioButton.setOnClickListener {
            onRecordatorioClick(paciente)
        }
    }

    override fun getItemCount() = pacientes.size
}

