package com.softli.health.models

import com.google.gson.annotations.SerializedName

data class Recordatorio (
    @SerializedName("idRecordatorio") val idRecordatorio: Int,
    @SerializedName("idEnfermero") val idEnfermero: Int,
    @SerializedName("medicamento") val medicamento: String,
    @SerializedName("cantidadMedicamento") val cantidad_medicamento: String,
    @SerializedName("fechaInicio") val fechaInicio: String,
    @SerializedName("fechaFin") val fechaFin: String,
    @SerializedName("estatus") val estatus: Boolean
)