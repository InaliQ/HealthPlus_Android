package com.softli.health.views.ui.alertas

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.softli.health.apiservice.AlertaApiService
import com.softli.health.models.Alerta
import com.softli.health.repositories.SessionRepository
import com.softli.health.views.PacientesActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AlertasViewModel(application: Application) : AndroidViewModel(application) {
    private val _alerta = MutableLiveData<List<Alerta>>()
    val alerta: LiveData<List<Alerta>> get() = _alerta
    @SuppressLint("StaticFieldLeak")
    private val context = application.applicationContext

    private val sessionRepository: SessionRepository = SessionRepository(application)

    init {
        val userId =
            sessionRepository.getPacienteId() ?: 1 // Usar un id de ejemplo si el userId es nulo
        loadAlerta(userId)
    }

    private fun loadAlerta(idPaciente: Int) {
        Log.d("PacientesViewModel", "Cargando pacientes con idPaciente: $idPaciente")
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(AlertaApiService::class.java)
        val call = apiService.getAlertas(idPaciente)

        call.enqueue(object : Callback<List<Alerta>> {
            override fun onResponse(call: Call<List<Alerta>>, response: Response<List<Alerta>>) {
                if (response.isSuccessful) {
                    _alerta.value = response.body()
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
        private const val BASE_URL = "http://10.0.2.2:5203/api/Alerta/"
    }
}
