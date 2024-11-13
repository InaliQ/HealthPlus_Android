package com.softli.health

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.softli.health.config.SessionManager
import com.softli.health.models.UsuarioModel
import com.softli.health.models.UsuarioUserRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    lateinit var contexto: Context
    lateinit var emailInputLayout: TextInputLayout
    lateinit var emailInput: TextInputEditText
    lateinit var contrasenaInputLayout: TextInputLayout
    lateinit var contrasenaInput: TextInputEditText
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        contexto = this
        sessionManager = SessionManager(this)
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
        val request = UsuarioUserRequest(nombreUsuario = usuario, contrasenia = contrasenia)
        RetrofitClient.instance.postLogin(request).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val stringJson = response.body()?.string()
                    val gson = Gson()
                    val usuarioModel = gson.fromJson(stringJson, UsuarioModel::class.java)
                    Log.d("MainActivity", "Response: $usuarioModel")
                    val intent = Intent(this@MainActivity, InicioActivity::class.java)
                    startActivity(intent)
                    finish()

                    if (usuarioModel != null ) {
                        sessionManager.savePacienteId(usuarioModel.idPaciente)
                        Toast.makeText(contexto, "Bienvenido", Toast.LENGTH_SHORT).show()

                        sessionManager.savePacienteId(usuarioModel.idPaciente)


                    } else {
                        Toast.makeText(contexto, "Error al obtener los datos del paciente", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(contexto, "Error: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
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
}