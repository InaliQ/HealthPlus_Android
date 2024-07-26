package com.softli.health.apiservice

import com.softli.health.models.Paciente
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface PacienteApiService {
    @GET("getPacientes")
    fun getPacientes(): Call<List<Paciente>>

    @POST("Paciente/agregarPaciente")
    fun agregarPaciente(@Body paciente: Paciente): Call<ResponseBody>
}