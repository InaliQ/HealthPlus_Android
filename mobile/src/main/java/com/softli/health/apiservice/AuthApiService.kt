package com.softli.health.apiservice

import com.softli.health.models.UsuarioUserRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("users/Auth/loginUser")
    fun postLogin(
        @Body request: UsuarioUserRequest
    ): Call<ResponseBody>
}


