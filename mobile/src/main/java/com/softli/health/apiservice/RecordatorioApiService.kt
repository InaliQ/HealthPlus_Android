package com.softli.health.apiservice

import com.softli.health.models.Recordatorio
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RecordatorioApiService {
    @GET("getRecordatorios/{idPaciente}")
    fun getRecordatorio(@Path("idPaciente") idPaciente: Int): Call<List<Recordatorio>>

    @GET("getRecordatorios")
    fun getRecordatorio(): Call<List<Recordatorio>>


    @POST("cambiarEstatus/{id}")
    fun cambiarEstatus(@Path("id") id: Int): Call<ResponseBody>


}