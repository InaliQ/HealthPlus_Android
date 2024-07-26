package com.softli.health.config

import android.content.Context
import com.google.gson.Gson
import com.softli.health.models.UsuarioModel

class SessionManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("idgsSession", Context.MODE_PRIVATE)
    fun saveUser(user: UsuarioModel) {
        val json = Gson().toJson(user)
        sharedPreferences.edit().putString("user", json).apply()
        sharedPreferences.edit().putInt("user_id", user.idUsuario).apply()
    }
    fun getUser(): UsuarioModel? {
        val json = sharedPreferences.getString("user", null)
        return Gson().fromJson(json, UsuarioModel::class.java)
    }
    fun getUserId(): String? {
        return sharedPreferences.getString("user_id", null)
    }
    fun clearSession() {
        sharedPreferences.edit().clear().apply()
    }
}