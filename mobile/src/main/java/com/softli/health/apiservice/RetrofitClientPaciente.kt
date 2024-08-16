package com.softli.health.apiservice

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClientPaciente {
    private const val BASE_URL = "http://10.0.2.2:5203/api/"
    val instance: PacienteApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(PacienteApiService::class.java)
    }
}