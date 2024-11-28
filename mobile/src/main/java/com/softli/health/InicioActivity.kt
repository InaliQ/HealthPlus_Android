package com.softli.health

import RetrofitClient
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.gson.Gson
import com.softli.health.apiservice.InfoApiService
import com.softli.health.apiservice.MandarApiService
import com.softli.health.config.SessionManager
import com.softli.health.models.AlertaRequest
import com.softli.health.models.RitmoMaxMinResponse
import com.softli.health.models.RitmoRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class InicioActivity : AppCompatActivity(), MessageClient.OnMessageReceivedListener {
    private lateinit var sessionManager: SessionManager
    private lateinit var ultimaAlertaTextView: TextView
    private lateinit var ultimoRecordatorioTextView: TextView
    private lateinit var ritmoTextView: TextView
    private lateinit var  estatusTextView: TextView

    private var lastExecutionTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.inicio_activity)

        sessionManager = SessionManager(this)
        ultimaAlertaTextView = findViewById(R.id.txtAlerta)
        ultimoRecordatorioTextView = findViewById(R.id.txtRecordatorio)
        ritmoTextView = findViewById(R.id.txtRitmo)
        estatusTextView = findViewById(R.id.txtEstatus)

        val idPaciente = sessionManager.getPacienteId()
        obtenerUltimaAlertaYRecordatorio(idPaciente)
        obtenerMaxMinRitmo(idPaciente)
        createNotificationChannel()
        Wearable.getMessageClient(this).addListener(this)
    }

    private fun obtenerUltimaAlertaYRecordatorio(idPaciente: Int) {
        val apiService = RetrofitClient.instanceInfo
        apiService.getUltimaAlertaRecordatorio(idPaciente).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    response.body()?.string()?.let { jsonResponse ->
                        try {
                            val jsonObject = JSONObject(jsonResponse)
                            val ultimaAlerta = jsonObject.getString("ultimaAlerta")
                            val ultimoRecordatorio = jsonObject.getString("ultimoRecordatorio")


                            val ultimaAlertaJson = JSONObject(ultimaAlerta)
                            val ultimoRecordatorioJson = JSONObject(ultimoRecordatorio)

                            val descripcion = ultimaAlertaJson.getString("descripcion")
                            val fecha = ultimaAlertaJson.getString("fechaHora")

                            val medicamento = ultimoRecordatorioJson.getString("medicamento")
                            val fechaInicio = ultimoRecordatorioJson.getString("fechaInicio")
                            val hora = fechaInicio.substring(11, 19)

                            val mensaje1 = "Descripcion: $descripcion\nFecha: $fecha"
                            val mensaje2 = "Medicamento: $medicamento\nHora: $hora"
                            ultimaAlertaTextView.text = "Última alerta: $mensaje1"
                            ultimoRecordatorioTextView.text ="Último recordatorio: $mensaje2"


                            val mensaje = "Recordatorio: $ultimoRecordatorio"
                            enviarMensajeAWearable("/recordatory", mensaje)
                            requestNotificationPermission("Recordatorio de medicamento", "Tienes medicamentos que tomar")
                        } catch (e: JSONException) {
                            Log.e("JSON Error", "Error al analizar JSON: ${e.message}")
                        }
                    }
                } else {
                    Toast.makeText(this@InicioActivity, "Error: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("API Error", "Error: ${t.message}")
                Toast.makeText(this@InicioActivity, "Error de red", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun enviarMensajeAWearable(path: String, message: String) {
        val nodeClient = Wearable.getNodeClient(this)
        val messageClient = Wearable.getMessageClient(this)
        nodeClient.connectedNodes.addOnSuccessListener { nodes ->
            for (node in nodes) {
                messageClient.sendMessage(node.id, path, message.toByteArray())
                    .addOnSuccessListener {
                        Log.d("WearableMessage", "Mensaje enviado correctamente: $message")
                    }
                    .addOnFailureListener { e ->
                        Log.e("WearableMessage", "Error al enviar mensaje: ${e.message}")
                    }
            }
        }
    }

    private fun obtenerMaxMinRitmo(idPaciente: Int) {
        val apiService = RetrofitClient.instanceInfo
        apiService.obtenerMaxMinRitmo(idPaciente).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    response.body()?.string()?.let { jsonString ->
                        Log.d("RitmoCardiaco", "JSON Response: $jsonString")
                        val gson = Gson()
                        val ritmoMaxMin = gson.fromJson(jsonString, RitmoMaxMinResponse::class.java)
                        val max = ritmoMaxMin.maximo ?: 0
                        val min = ritmoMaxMin.minimo ?: 0
                        sessionManager.saveMaxMin(max, min)
                        enviarMensajeAWearable("/maxmin", "$max,$min")
                    }
                } else {
                    Log.e("RitmoCardiaco", "Error al obtener datos: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("RitmoCardiaco", "Error en la conexión", t)
            }
        })
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        CoroutineScope(Dispatchers.IO).launch {
            if (messageEvent.path == "/hear_rate") {
                val message = String(messageEvent.data)
                val good = "Good 0.0"
                val medium = "Regular -.-"
                val ritmo = message.substringAfter("Frecuencia cardíaca: ").toIntOrNull()
                ritmo?.let {
                    ritmoTextView.text = "Ritmo cardíaco: $it"
                    val currentTime = System.currentTimeMillis()
                    if (TimeUnit.MILLISECONDS.toMinutes(currentTime - lastExecutionTime) >= 10) {
                        lastExecutionTime = currentTime
                        guardarRitmoCardiaco(sessionManager.getPacienteId(), it)
                    }

                }

            }
            if (messageEvent.path == "/emergency_status") {
                val message = String(messageEvent.data)
                val descripcion = "Emergencia con el paciente"
                val idPaciente = sessionManager.getPacienteId()
                guardarAlerta(idPaciente, descripcion)

                val intent = Intent(this@InicioActivity,InfoActivity::class.java)
                startActivity(intent)
            }

            if (messageEvent.path == "/confirmation") {
                val id = String(messageEvent.data)
                val idRecordatorio = id.toIntOrNull() // Convertir el id a un número entero (puede ser null)
                if (idRecordatorio != null) {
                    confirmarRecodatorio(idRecordatorio) // Llamamos a la función si el id es válido
                } else {
                    Log.e("Recordatorio", "Error: El ID del recordatorio es inválido o nulo")
                }
            }




        }
    }

    private fun guardarRitmoCardiaco(idPaciente: Int, medicion: Int) {
        val request = RitmoRequest(idPaciente, medicion)
        RetrofitClient.instaceMandarInfo.guardarRitmoCardiaco(request).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("RitmoCardiaco", "Ritmo guardado correctamente")
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("RitmoCardiaco", "Error al guardar el ritmo", t)
            }
        })
    }

    private fun guardarAlerta(idPaciente: Int, descripcion: String) {
        val request = AlertaRequest(idPaciente = idPaciente, descripcion = descripcion)
        RetrofitClient.instaceMandarInfo.agregarAlerta(request).enqueue(object : Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("Alerta", "Alerta guardada correctamente")
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Alerta", "Error al guardar la alerta", t)
            }

        })
    }

    private fun confirmarRecodatorio(idRecordatorio: Int) {
        RetrofitClient.instaceMandarInfo.cambiarEstatus(idRecordatorio).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    Log.d("Recordatorio", "Recordatorio confirmado correctamente")
                } else {
                    Log.e("Recordatorio", "Error al confirmar el recordatorio: ${response.errorBody()?.string()}")
                }
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("Recordatorio", "Error al confirmar el recordatorio", t)
            }
        })
    }


    private fun requestNotificationPermission(title: String, message: String) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_CODE_POST_NOTIFICATIONS
            )
        } else {
            showNotification(title, message)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Canal de Notificaciones",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Descripción del Canal"
            }
            val manager: NotificationManager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(title: String, message: String) {
        val intent = Intent(this, InicioActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(this@InicioActivity, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notify(NOTIFICATION_ID, builder.build())
            }
        }
    }

    companion object {
        private const val CHANNEL_ID = "canal_de_notificaciones"
        private const val NOTIFICATION_ID = 1
        private const val REQUEST_CODE_POST_NOTIFICATIONS = 1001
    }
}
