package com.softli.health.models

import com.google.gson.annotations.SerializedName


data class RitmoMaxMinResponse(
    @SerializedName("max") val maximo: Int?,
    @SerializedName("min") val minimo: Int?
)
data class RitmoRequest(
    val idPaciente: Int,
    val Medicion: Int
)
