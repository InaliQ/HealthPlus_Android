package com.softli.health.models

import com.google.gson.annotations.SerializedName

data class UsuarioModel(
    @SerializedName("idPaciente") val idPaciente: Int
)

data class UltimaAlertaRecordatorio(
    val ultimaAlerta: Alerta?,
    val ultimoRecordatorio: Recordatorio?
)

data class UsuarioUserRequest(
    val nombreUsuario: String,
    val contrasenia: String
)