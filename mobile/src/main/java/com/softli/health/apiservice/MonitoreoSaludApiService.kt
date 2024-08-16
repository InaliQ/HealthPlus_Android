package com.softli.health.apiservice

import com.softli.health.models.MonitoreoSalud
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface MonitoreoSaludApiService {
    @POST("MonitoreSalud/registrarMonitoreoSalud")
    fun registrarMonitoreoSalud(@Body monitoreoSalud: MonitoreoSalud): Call<String>

}