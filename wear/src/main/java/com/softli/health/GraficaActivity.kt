package com.softli.health

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.softli.health.EmergenciaActivity
import com.softli.health.R

class GraficaActivity : ComponentActivity(), DataClient.OnDataChangedListener {
    private lateinit var imgGrafica: ImageView
    private lateinit var barraProgreso: ProgressBar
    private lateinit var txtLatidos: TextView

    private val handler = Handler(Looper.getMainLooper())
    private var currentIndex = 0
    private val updateInterval = 1000L
    private val heartRates = intArrayOf(60, 62, 63, 65, 66, 68, 69, 71, 73, 75, 76, 78, 79, 81, 83, 84, 86, 87, 89)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.grafica_activity)

        imgGrafica = findViewById(R.id.imgGrafica)
        barraProgreso = findViewById(R.id.barraProgreso)
        txtLatidos = findViewById(R.id.txtLatidos)

        handler.post(updateHeartRate)
        Wearable.getDataClient(this).addListener(this)
    }

    private val updateHeartRate = object : Runnable {
        override fun run() {
            if (currentIndex < heartRates.size) {
                val heartRate = heartRates[currentIndex]
                txtLatidos.text = "Frecuencia cardíaca: $heartRate"
                barraProgreso.progress = heartRate

                // Cambiar de actividad si los latidos son 87 o más
                if (heartRate >= 87) {
                    sendAlertToPhone(heartRate)
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

    private fun sendAlertToPhone(heartRate: Int) {
        val dataMap = PutDataMapRequest.create("/heart_rate").run {
            dataMap.putInt("heart_rate", heartRate)
            asPutDataRequest()
        }
        Wearable.getDataClient(this).putDataItem(dataMap)
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        // Manejo de datos recibidos si es necesario
    }

    override fun onDestroy() {
        handler.removeCallbacks(updateHeartRate)
        Wearable.getDataClient(this).removeListener(this)
        super.onDestroy()
    }
}
