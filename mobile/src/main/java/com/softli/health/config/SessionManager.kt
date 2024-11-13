package com.softli.health.config

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.softli.health.models.UsuarioModel

class SessionManager(context: Context) {
        private val prefs: SharedPreferences = context.getSharedPreferences("health_prefs", Context.MODE_PRIVATE)

        fun savePacienteId(idPaciente: Int) {
            prefs.edit().putInt("idPaciente", idPaciente).apply()
        }

        fun getPacienteId(): Int {
            return prefs.getInt("idPaciente", -1)
        }

        fun saveMaxMin(max: Int, min: Int) {
            prefs.edit().putInt("max", max).apply()
            prefs.edit().putInt("min", min).apply()
        }
        fun getMaxMin(): Pair<Int, Int> {
            val max = prefs.getInt("max", -1)
            val min = prefs.getInt("min", -1)
            return Pair(max, min)
        }

    }
