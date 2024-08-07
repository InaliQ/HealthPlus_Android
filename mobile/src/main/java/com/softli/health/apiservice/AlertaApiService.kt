package com.softli.health.apiservice

import com.softli.health.models.Alerta
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface AlertaApiService {
    @GET("getAlerta/{idPaciente}")
    fun getAlertas(@Path("idPaciente") idPaciente: Int): Call<List<Alerta>>
}