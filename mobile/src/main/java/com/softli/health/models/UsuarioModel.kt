package com.softli.health.models

import com.google.gson.annotations.SerializedName

data class UsuarioModel(
    @SerializedName("idUsuario") val idUsuario: Int,
    @SerializedName("usuario1") val usuario: String,
    @SerializedName("idPersona") val idPersona: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("primerApellido") val primerApellido: String,
    @SerializedName("segundoApellido") val segundoApellido: String,
    @SerializedName("enfermero") val enfermero: Enfermero
)