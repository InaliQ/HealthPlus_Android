package com.softli.health.apiservice

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApiService {
    @POST("Log/in")
    fun postLogin(
        @Query("usuario") usuario: String,
        @Query("contrasenia") contrasenia: String
    ): Call<ResponseBody>
}