package com.softli.health.models

import com.google.gson.annotations.SerializedName

data class Enfermero (
    @SerializedName("idEnfermero") val idEnfermero: Int,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("numEnfermero") val numEnfermero: String,
)