package com.softli.health.apiservice

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path


interface InfoApiService {
    @GET("Recordatorio/UltimaAlertaRecordatorio/{idPaciente}")
    fun getUltimaAlertaRecordatorio(@Path("idPaciente") idPaciente: Int): Call<ResponseBody>

    @GET("Ritmo/ObtenerMaxMinRitmo/{idPaciente}")
    fun obtenerMaxMinRitmo(@Path("idPaciente") idPaciente: Int): Call<ResponseBody>

}
