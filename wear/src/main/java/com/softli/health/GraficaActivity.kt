package com.softli.health

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.util.Log
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.softli.health.EmergenciaActivity
import com.softli.health.R
import org.json.JSONException
import org.json.JSONObject

class GraficaActivity : ComponentActivity(), MessageClient.OnMessageReceivedListener, SensorEventListener {

    private lateinit var imgGrafica: ImageView
    private lateinit var barraProgreso: ProgressBar
    private lateinit var txtLatidos: TextView
    private lateinit var sensorManager: SensorManager
    private var heartRateSensor: Sensor? = null
    private val maxMin = IntArray(2)

    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("MainActivity", "Permiso de sensores corporales concedido")
                initializeSensor()
            } else {
                Log.d("MainActivity", "Permiso de sensores corporales denegado")
                Toast.makeText(this, "Permiso de sensores corporales denegado", Toast.LENGTH_SHORT).show()
            }
        }

    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.grafica_activity)

        imgGrafica = findViewById(R.id.imgGrafica)
        barraProgreso = findViewById(R.id.barraProgreso)
        txtLatidos = findViewById(R.id.txtLatidos)

        when {
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.BODY_SENSORS) == PackageManager.PERMISSION_GRANTED -> {
                Log.d("MainActivity", "Permiso de sensores corporales ya concedido")
                initializeSensor()
            }
            else -> {
                Log.d("MainActivity", "Solicitando permiso de sensores corporales")
                requestPermissionLauncher.launch(android.Manifest.permission.BODY_SENSORS)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        Wearable.getMessageClient(this).addListener(this)
    }

    override fun onPause() {
        super.onPause()
        Wearable.getMessageClient(this).removeListener(this)
    }

    fun updateRate(heartRate: Int) {
        Log.d("Runnable", "Actualizando UI con frecuencia cardíaca: $heartRate")
        Log.d("Runnable","Frecuencia Cardíaca: $heartRate")

        txtLatidos.text = "Frecuencia cardíaca: $heartRate"
        barraProgreso.progress = heartRate

        sendMessageToPhone("Frecuencia cardíaca: $heartRate")
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val max = preferences.getInt("ritmo_max", 80)
        val min = preferences.getInt("ritmo_min", 60)
        Log.d("Runnable", "Máximo: $max, Mínimo: $min")

        if (heartRate >= max) {
            sensorManager.unregisterListener(this)
            val intent = Intent(this@GraficaActivity, EmergenciaActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            currentIndex++
        }
    }



    private fun sendMessageToPhone(message: String) {
        Wearable.getNodeClient(this).connectedNodes.addOnSuccessListener { nodes ->
            for (node in nodes) {
                Wearable.getMessageClient(this)
                    .sendMessage(node.id, "/hear_rate", message.toByteArray())
                    .addOnSuccessListener {
                        Log.d("GraficaActivity", "Message sent: $message")
                    }.addOnFailureListener { e ->
                        Log.e("GraficaActivity", "Failed to send message", e)
                    }
            }
        }
    }



    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.path == "/recordatory") {
            val message = String(messageEvent.data)
            Log.d("Recordatory", "Message received: $message")
            val jsonMessage = message.removePrefix("Recordatorio: ").trim()
            try {
                val jsonObject = JSONObject(jsonMessage)
                val medicamento = jsonObject.getString("medicamento")
                val fechaInicio = jsonObject.getString("fechaInicio")
                val idRecordatorio = jsonObject.getInt("idRecordatorio")
                val hora = fechaInicio.split("T")[1] // "10:53:00"
                val mensajeFormateado = "$medicamento a las $hora con id:$idRecordatorio"
                val regex = Regex("""(.+?) a las (.+?) con id:(\d+)""")
                val matchResult = regex.find(mensajeFormateado)

                if (matchResult != null) {
                    val (medicamentoExtraido, horaExtraida, idRecordatorioExtraido) = matchResult.destructured
                    val mensajeCorto = "$medicamentoExtraido\n$horaExtraida"

                    val intent = Intent(this, RecordatorioActivity::class.java).apply {
                        putExtra("recordatorio_message", mensajeCorto)
                        putExtra("id_recordatorio", idRecordatorioExtraido.toInt())
                    }
                    startActivity(intent)
                } else {
                    Log.e("Recordatory", "Formato de mensaje no válido: $mensajeFormateado")
                }
            } catch (e: JSONException) {
                Log.e("Recordatory", "Error al parsear el mensaje JSON: ${e.message}")
            }
        }
        if (messageEvent.path == "/maxmin") {
            val maxMinValues = String(messageEvent.data).split(",")
            val max = maxMinValues[0].toIntOrNull() ?: 0
            val min = maxMinValues[1].toIntOrNull() ?: 0

            // Guardar los valores en SharedPreferences para futuras comparaciones
            val preferences = PreferenceManager.getDefaultSharedPreferences(this)
            preferences.edit().putInt("ritmo_max", max).putInt("ritmo_min", min).apply()
            }
    }




    private fun initializeSensor() {
        Log.d("MainActivity", "Inicializando el sensor de frecuencia cardíaca")
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
        if (heartRateSensor != null) {
            Log.d("MainActivity", "Sensor de frecuencia cardíaca encontrado, registrando listener")
            sensorManager.registerListener(this, heartRateSensor, SensorManager.SENSOR_DELAY_NORMAL)
        } else {
            Log.d("MainActivity", "Sensor de frecuencia cardíaca no encontrado")
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null && event.sensor.type == Sensor.TYPE_HEART_RATE) {
            val heartRate = event.values[0].toInt()
            Log.d("MainActivity", "Frecuencia cardíaca recibida: $heartRate")
            updateRate(heartRate)
        } else {
            Log.d("MainActivity", "Evento de sensor no es de tipo frecuencia cardíaca")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d("MainActivity", "onAccuracyChanged llamado, precisión: $accuracy")
    }

}