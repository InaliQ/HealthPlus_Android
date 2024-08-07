package com.softli.health.views.ui.recordatorio

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.softli.health.R

import com.softli.health.views.ui.recordatorio.placeholder.PlaceholderContent.PlaceholderItem
import com.softli.health.databinding.FragmentRecordatorioBinding
import com.softli.health.models.Recordatorio

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyRecordatorioRecyclerViewAdapter(
    private val recordatorios: List<Recordatorio>) : RecyclerView.Adapter<MyRecordatorioRecyclerViewAdapter.RecordatorioViewHolder>() {

    class RecordatorioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val medicamentoTextView: TextView = itemView.findViewById(R.id.text_value_medicamento)
        val cantidadTextView: TextView = itemView.findViewById(R.id.text_value_cantidad)
        val fechaInicioTextView: TextView = itemView.findViewById(R.id.text_value_fecha_inicio)
        val fechaFinTextView: TextView = itemView.findViewById(R.id.text_value_fecha_fin)
        val estatusTextView: TextView = itemView.findViewById(R.id.text_value_estatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordatorioViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.fragment_recordatorio, parent, false)
        return RecordatorioViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: RecordatorioViewHolder, position: Int) {
        val item = recordatorios[position]
        holder.medicamentoTextView.text = item.medicamento
        holder.cantidadTextView.text = item.cantidad_medicamento
        holder.fechaInicioTextView.text = item.fecha_inicio
        holder.fechaFinTextView.text = item.fecha_fin
        holder.estatusTextView.text = if(item.estatus)  "Atendido" else "Sin atender"
    }

    override fun getItemCount(): Int = recordatorios.size

}