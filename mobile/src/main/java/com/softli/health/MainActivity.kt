package com.softli.health

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.softli.health.apiservice.RetrofitClient
import com.softli.health.config.SessionManager
import com.softli.health.models.UsuarioModel
import com.softli.health.repositories.SessionRepository
import com.softli.health.views.PacientesActivity
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), DataClient.OnDataChangedListener {
    lateinit var contexto: Context
    lateinit var emailInputLayout: TextInputLayout
    lateinit var emailInput: TextInputEditText
    lateinit var contrasenaInputLayout: TextInputLayout
    lateinit var contrasenaInput: TextInputEditText
    lateinit var sessionManager: SessionManager
    lateinit var sessionRepository: SessionRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Wearable.getDataClient(this).addListener(this)
        contexto = this
        sessionManager = SessionManager(this)
        sessionRepository = SessionRepository(this)
        contrasenaInputLayout = findViewById<TextInputLayout>(R.id.contrasenaInputLayout)
        contrasenaInput = findViewById<TextInputEditText>(R.id.contrasenaEditText)
        emailInputLayout = findViewById<TextInputLayout>(R.id.emailInputLayout)
        emailInput = findViewById<TextInputEditText>(R.id.emailEditText)
        val emailParam = intent.getStringExtra("email")
        if (emailParam != null) {
            emailInput.setText(emailParam.toString())
        }
        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener {
            if (formValido()) {
                login(emailInput.text.toString(), contrasenaInput.text.toString())
            } else {
                Toast.makeText(this, "Existe información incorrecta", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun login(usuario: String, contrasenia: String) {
        Toast.makeText(this, "Iniciando sesión...", Toast.LENGTH_SHORT).show()
//        val intent = Intent(this@MainActivity, PacientesActivity::class.java)
//        startActivity(intent)
//        finish()
        RetrofitClient.instance.postLogin(usuario, contrasenia).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    var stringJson = response.body()?.string();
                    val gson = Gson()
                    val usuario = gson.fromJson(stringJson, UsuarioModel::class.java)
                    sessionManager.saveUser(usuario)
                    sessionRepository.saveEnfermero(usuario.enfermero)
                    Toast.makeText(contexto, "Bienvenido ${usuario.nombre}", Toast.LENGTH_SHORT)
                        .show()
                    val intent = Intent(this@MainActivity, PacientesActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        contexto,
                        "Error: ${response.errorBody()?.string()}",
                        Toast.LENGTH_SHORT
                    ).show();
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Handle failure
                Log.e("MainActivity", "Failure: ${t.message}")
            }
        })
    }

    fun formValido(): Boolean {
        var valido = true
        if (contrasenaInput.text.toString().isEmpty()) {
            contrasenaInputLayout.error = "Campo obligatorio"
            valido = false
        } else {
            contrasenaInputLayout.error = ""
        }
        if (emailInput.text.toString().isEmpty()) {
            emailInputLayout.error = "Campo obligatorio"
            valido = false
        } else {
            emailInputLayout.error = ""
        }
        return valido
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Log.d("MainActivity", "onDataChanged: $dataEvents")
        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val dataItem = event.dataItem
                if (dataItem.uri.path == "/heart_rate") {
                    val dataMap = DataMapItem.fromDataItem(dataItem).dataMap
                    val heartRate = dataMap.getInt("heart_rate")
                    actualizarHeartRate(heartRate)
                }
            }
        }
    }

    private fun actualizarHeartRate(heartRate: Int) {
        Log.d("MainActivity", "Pulso cardíaco actualizado: $heartRate")
    }
}