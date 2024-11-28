package com.softli.health.apiservice

import com.softli.health.models.AlertaRequest
import com.softli.health.models.RitmoMaxMinResponse
import com.softli.health.models.RitmoRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MandarApiService {

    @POST("Ritmo/guardar")
    fun guardarRitmoCardiaco(@Body request: RitmoRequest): Call<ResponseBody>


    @POST("Recordatorio/CompletarRecordatorio/{idRecordatorio}")
    fun cambiarEstatus(@Path("idRecordatorio") idRecordatorio: Int): Call<String>

    @POST("Alerta/agregar")
    fun agregarAlerta(@Body request: AlertaRequest): Call<ResponseBody>
}
