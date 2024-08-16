package com.softli.health.models

import com.google.gson.annotations.SerializedName

data class MonitoreoSalud (
    @SerializedName("idMonitoreo") val idMonitoreo: Int,
    @SerializedName("idPaciente") val idPaciente: Int,
    @SerializedName("fechaHora") val fechaHora: String,
    @SerializedName("ritmoCardiaco") val ritmoCardiaco: Int,
    @SerializedName("tipo") val tipo: String
)