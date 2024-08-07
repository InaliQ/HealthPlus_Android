package com.softli.health.views.ui.recordatorio

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.softli.health.apiservice.RecordatorioApiService
import com.softli.health.models.Recordatorio
import com.softli.health.repositories.SessionRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RecordatorioViewModel(application: Application) : AndroidViewModel(application) {
    private val _recordatorios = MutableLiveData<List<Recordatorio>>()
    val recordatorios: LiveData<List<Recordatorio>> get() = _recordatorios
    private val sessionRepository: SessionRepository = SessionRepository(application)

    init {
        val userId = sessionRepository.getPacienteId() ?: 1 // Usar un id de ejemplo si el userId es nulo
        loadRecordatorios(userId)
    }

    private fun loadRecordatorios(userId: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(RecordatorioApiService::class.java)
        val call = apiService.getRecordatorio(userId)

        call.enqueue(object : Callback<List<Recordatorio>>{
            override fun onResponse(call: Call<List<Recordatorio>>, response: Response<List<Recordatorio>>) {
                if (response.isSuccessful) {
                    _recordatorios.value = response.body()
                } else {
                    Log.e("RecordatorioViewModel", "Error en la respuesta: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Recordatorio>>, t: Throwable) {
                Log.e("RecordatorioViewModel", "Error en la llamada a la API", t)
            }

        })

    }

    companion object {
        private const val BASE_URL = "http://10.0.2.2:5042/api/Recordatorio/"
    }
}