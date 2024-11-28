package com.softli.health.models

import com.google.gson.annotations.SerializedName

data class AlertaRequest(
    val descripcion: String,
    val idPaciente: Int
)
data class Alerta (
    @SerializedName("idAlerta") val idAlerta: Int,
    @SerializedName("idPaciente") val idPaciente: Int,
    @SerializedName("fechaHora") val fechaHora: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("nombre") val nombre: String,
)