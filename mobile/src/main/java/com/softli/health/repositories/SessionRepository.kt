package com.softli.health.repositories

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.softli.health.models.Enfermero
import com.softli.health.models.Paciente
import com.softli.health.models.Recordatorio
import com.softli.health.models.UsuarioModel

class SessionRepository(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("idgsSession", Context.MODE_PRIVATE)

    fun saveUser(user: UsuarioModel) {
        val json = Gson().toJson(user)
        sharedPreferences.edit().putString("user", json).apply()
        sharedPreferences.edit().putInt("user_id", user.idUsuario).apply()
    }

    fun savePaciente(paciente: Paciente) {
        val json = Gson().toJson(paciente)
        sharedPreferences.edit().putString("paciente", json).apply()
        sharedPreferences.edit().putInt("paciente_id", paciente.idPaciente).apply()
    }

    fun saveEnfermero(enfermero: Enfermero) {
        val json = Gson().toJson(enfermero)
        sharedPreferences.edit().putString("enfermero", json).apply()
        sharedPreferences.edit().putInt("enfermero_id", enfermero.idEnfermero).apply()
    }

    fun saveRecordatorios(recordatorios: List<Recordatorio>) {
        val json = Gson().toJson(recordatorios)
        sharedPreferences.edit().putString("recordatorios", json).apply()
    }

    fun getRecordatorios(): List<Recordatorio>? {
        val json = sharedPreferences.getString("recordatorios", null)
        val type = object : TypeToken<List<Recordatorio>>() {}.type
        return Gson().fromJson(json, type)
    }

    fun cleanRecordatorios() {
        sharedPreferences.edit().remove("recordatorios").apply()
    }

    fun getEnfermeroId(): Int? {
        return sharedPreferences.getInt("enfermero_id", -1)
    }

    fun getPacienteId(): Int? {
        return sharedPreferences.getInt("paciente_id", -1)
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

    fun clearSession() {
        sharedPreferences.edit().clear().apply()
    }
}
