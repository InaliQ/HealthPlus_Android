package com.softli.health.apiservice

import com.softli.health.models.Alerta
import retrofit2.Call
import retrofit2.http.GET

interface AlertaApiService {
    @GET("getAlertas")
    fun getAlertas(): Call<List<Alerta>>
}