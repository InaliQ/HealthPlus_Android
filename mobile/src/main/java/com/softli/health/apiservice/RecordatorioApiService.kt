package com.softli.health.apiservice

import com.softli.health.models.Recordatorio
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RecordatorioApiService {
    @GET("getRecordatorios/{idPaciente}")
    fun getRecordatorio(@Path("idPaciente") idPaciente: Int): Call<List<Recordatorio>>

    @GET("getRecordatorios")
    fun getRecordatorio(): Call<List<Recordatorio>>

    @FormUrlEncoded
    @POST("cambiarEstatus")
    fun cambiarEstatus(@Field("id") id: Int): Call<String>
}