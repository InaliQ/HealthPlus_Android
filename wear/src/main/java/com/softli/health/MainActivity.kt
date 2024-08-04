package com.softli.health

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.samsung.android.sdk.healthdata.HealthConnectionErrorResult
import com.samsung.android.sdk.healthdata.HealthConstants
import com.samsung.android.sdk.healthdata.HealthDataResolver
import com.samsung.android.sdk.healthdata.HealthDataStore
import com.softli.health.R

class MainActivity : ComponentActivity() {
    private lateinit var mStore: HealthDataStore
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        //mStore = HealthDataStore(this, mConnectionListener)
        //mStore.connectService()

        val intent = Intent(this, GraficaActivity::class.java)
        startActivity(intent)
    }

    private val mConnectionListener = object : HealthDataStore.ConnectionListener {
        override fun onConnected() {
            // Conexión exitosa
            readHeartRateData()
        }

        override fun onConnectionFailed(error: HealthConnectionErrorResult?) {
            // Manejar error de conexión
        }

        override fun onDisconnected() {
            // Desconectado del servicio
        }
    }

    private fun readHeartRateData() {
        val resolver = HealthDataResolver(mStore, null)
        val request = HealthDataResolver.ReadRequest.Builder()
            .setDataType(HealthConstants.HeartRate.HEART_RATE)
            .build()

        try {
            resolver.read(request).setResultListener { result ->
                try {
                    for (data in result) {
                        val heartRate = data.getFloat(HealthConstants.HeartRate.HEART_RATE)
                        // Procesar el dato de frecuencia cardíaca
                        println("Frecuencia cardíaca: $heartRate")
                    }
                } finally {
                    result.close()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
