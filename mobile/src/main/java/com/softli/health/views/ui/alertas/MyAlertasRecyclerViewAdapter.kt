package com.softli.health.views.ui.alertas

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.softli.health.R
import com.softli.health.models.Alerta
import com.softli.health.views.ui.alertas.placeholder.PlaceholderContent.PlaceholderItem

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyAlertasRecyclerViewAdapter(private val alertas: List<Alerta>) :
    RecyclerView.Adapter<MyAlertasRecyclerViewAdapter.AlertasViewHolder>() {

    class AlertasViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreTextView: TextView = itemView.findViewById(R.id.text_name)
        val edadTextView: TextView = itemView.findViewById(R.id.text_id)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertasViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.fragment_item_alerta, parent, false)
        return AlertasViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AlertasViewHolder, position: Int) {
        val alerta = alertas[position]
        holder.nombreTextView.text = alerta.nombre
        holder.edadTextView.text = alerta.descripcion
    }

    override fun getItemCount() = alertas.size
}