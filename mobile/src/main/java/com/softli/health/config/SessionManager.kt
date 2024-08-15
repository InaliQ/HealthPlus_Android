package com.softli.health.config

import android.content.Context
import com.google.gson.Gson
import com.softli.health.models.Paciente
import com.softli.health.models.UsuarioModel

class SessionManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("idgsSession", Context.MODE_PRIVATE)
    fun saveUser(user: UsuarioModel) {
        val json = Gson().toJson(user)
        sharedPreferences.edit().putString("user", json).apply()
        sharedPreferences.edit().putInt("user_id", user.idUsuario).apply()
        sharedPreferences.edit().putInt("enfermero_id", user.enfermero.idEnfermero).apply()
    }
    fun savePaciente(paciente: Paciente) {
        val json = Gson().toJson(paciente)
        sharedPreferences.edit().putString("paciente", json).apply()
        sharedPreferences.edit().putInt("paciente_id", paciente.idPaciente).apply()
    }
    fun cleanPaciente() {
        sharedPreferences.edit().remove("paciente").apply()
        sharedPreferences.edit().remove("paciente_id").apply()
    }
    fun getUser(): UsuarioModel? {
        val json = sharedPreferences.getString("user", null)
        return Gson().fromJson(json, UsuarioModel::class.java)
    }
    fun getUserId(): Int? {
        return sharedPreferences.getInt("user_id", -1)
    }
    fun getEnfermeroId(): Int? {
        return sharedPreferences.getInt("enfermero_id", -1)
    }
    fun clearSession() {
        sharedPreferences.edit().clear().apply()
    }
}