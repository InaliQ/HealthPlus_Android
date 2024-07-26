package com.softli.health.views.ui.alertas

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.softli.health.apiservice.AlertaApiService
import com.softli.health.apiservice.PacienteApiService
import com.softli.health.models.Alerta
import com.softli.health.models.Paciente
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AlertasViewModel: ViewModel() {
    private val _alertas = MutableLiveData<List<Alerta>>()
    val alertas: LiveData<List<Alerta>> get() = _alertas

    init {
        loadAlertas()
    }

    private fun loadAlertas() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(AlertaApiService::class.java)
        val call = apiService.getAlertas()

        call.enqueue(object : Callback<List<Alerta>> {
            override fun onResponse(call: Call<List<Alerta>>, response: Response<List<Alerta>>) {
                if (response.isSuccessful) {
                    _alertas.value = response.body()
                } else {
                    Log.e("PacientesViewModel", "Error en la respuesta: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Alerta>>, t: Throwable) {
                Log.e("PacientesViewModel", "Error en la llamada a la API", t)
            }
        })
    }

    companion object {
        private const val BASE_URL = "http://10.0.2.2:5042/api/Alerta/"
    }
}