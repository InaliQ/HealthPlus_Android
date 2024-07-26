package com.softli.health.views.ui.pacientes

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.softli.health.apiservice.PacienteApiService
import com.softli.health.models.Paciente
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PacientesViewModel : ViewModel() {
    private val _pacientes = MutableLiveData<List<Paciente>>()
    val pacientes: LiveData<List<Paciente>> get() = _pacientes

    init {
        loadPacientes()
    }

    private fun loadPacientes() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(PacienteApiService::class.java)
        val call = apiService.getPacientes()

        call.enqueue(object : Callback<List<Paciente>> {
            override fun onResponse(call: Call<List<Paciente>>, response: Response<List<Paciente>>) {
                if (response.isSuccessful) {
                    _pacientes.value = response.body()
                } else {
                    Log.e("PacientesViewModel", "Error en la respuesta: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Paciente>>, t: Throwable) {
                Log.e("PacientesViewModel", "Error en la llamada a la API", t)
            }
        })
    }

    companion object {
        private const val BASE_URL = "http://10.0.2.2:5042/api/Paciente/"
    }
}
