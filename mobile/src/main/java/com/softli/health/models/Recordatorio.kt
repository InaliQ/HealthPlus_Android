package com.softli.health.models

import com.google.gson.annotations.SerializedName

data class Recordatorio (
    @SerializedName("idRecordatorio") val idRecordatorio: Int,
    @SerializedName("idEnfermero") val idEnfermero: Int,
    @SerializedName("medicamento") val medicamento: String,
    @SerializedName("cantidad_medicamento") val cantidad_medicamento: String,
    @SerializedName("idPaciente") val idPaciente: Int,
    @SerializedName("fecha") val fecha: String,
    @SerializedName("fecha_inicio") val fecha_inicio: String,
    @SerializedName("fecha_fin") val fecha_fin: String,
    @SerializedName("estatus") val estatus: Boolean
    )