package com.softli.health.models

import com.google.gson.annotations.SerializedName

data class Paciente (
    @SerializedName("idPaciente")  val idPaciente: Int,
    @SerializedName("numPaciente") val numPaciente: String,
    @SerializedName("altura") val altura: String,
    @SerializedName("peso") val peso: String,
    @SerializedName("tipoSangre") val tipoSangre: String,
    @SerializedName("estatus") val estatus: Boolean,
    @SerializedName("idPersona") val idPersona: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("primerApellido") val primerApellido: String,
    @SerializedName("segundoApellido") val segundoApellido: String,
    @SerializedName("telefono") val telefono: String,
    @SerializedName("calle") val calle: String,
    @SerializedName("numero") val numero: String,
    @SerializedName("codigoPostal") val codigoPostal: String,
    @SerializedName("colonia") val colonia: String
)