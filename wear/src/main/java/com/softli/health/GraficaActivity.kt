package com.softli.health

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.ComponentActivity
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

class GraficaActivity : ComponentActivity(), MessageClient.OnMessageReceivedListener {

    private lateinit var imgGrafica: ImageView
    private lateinit var barraProgreso: ProgressBar
    private lateinit var txtLatidos: TextView

    private val handler = Handler(Looper.getMainLooper())
    private var currentIndex = 0
    private val updateInterval = 1000L
    private val heartRates =
        intArrayOf(60, 62, 63, 65, 66, 68, 69, 71, 73, 75, 76, 78, 79, 81, 83, 84, 86, 87, 89)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.grafica_activity)

        imgGrafica = findViewById(R.id.imgGrafica)
        barraProgreso = findViewById(R.id.barraProgreso)
        txtLatidos = findViewById(R.id.txtLatidos)

    }

    override fun onResume() {
        super.onResume()
        handler.post(updateHeartRate)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(updateHeartRate)
    }

    private val updateHeartRate = object : Runnable {
        override fun run() {
            if (currentIndex < heartRates.size) {
                val heartRate = heartRates[currentIndex]
                txtLatidos.text = "Frecuencia cardíaca: $heartRate"
                barraProgreso.progress = heartRate

                // Enviar el valor del pulso cardíaco continuamente
                sendMessageToPhone("Frecuencia cardíaca: $heartRate")

                // Si los latidos son 87 o más, iniciar la actividad de emergencia
                if (heartRate >= 87) {
                    val intent = Intent(this@GraficaActivity, EmergenciaActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    currentIndex++
                    handler.postDelayed(this, updateInterval)
                }
            }
        }
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.path == "/recordatory") {
            val message = String(messageEvent.data)
            Log.d("Recordatory", "Message received: $message")
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

}
