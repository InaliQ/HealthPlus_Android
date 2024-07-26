package com.softli.health.models

import com.google.gson.annotations.SerializedName

data class PersonaModel(
    @SerializedName("idPersona") val idPersona: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("primer_apellido") val primer_apellido: String,
    @SerializedName("segundo_apellido") val segundo_apellido: String,
    @SerializedName("telefono") val telefono: String,
    @SerializedName("calle") val calle: String,
    @SerializedName("numero") val numero: String,
    @SerializedName("codigo_postal") val codigo_postal: String,
    @SerializedName("colonia") val colonia: String,
)