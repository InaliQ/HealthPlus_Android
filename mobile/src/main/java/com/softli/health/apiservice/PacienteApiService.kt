package com.softli.health.apiservice

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface PacienteApiService {
    @GET("getPacientes/{idEnfermero}")
    fun getPacientes(@Path("idEnfermero") idPaciente: Int): Call<List<Paciente>>

    @POST("Paciente/agregarPaciente")
    fun agregarPaciente(@Body paciente: Paciente): Call<ResponseBody>
}