package com.softli.health

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable

class MainActivity : ComponentActivity(), DataClient.OnDataChangedListener {

    private lateinit var textViewSensorData: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        //textViewSensorData = findViewById(R.id.textViewSensorData)

        // Connect to the DataClient to listen to data changes
        Wearable.getDataClient(this).addListener(this)
        cambiarIntent();
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val dataMapItem = DataMapItem.fromDataItem(event.dataItem)
                val heartRate = dataMapItem.dataMap.getFloat("heartRate")

                // Update the TextView with the heart rate data
                runOnUiThread {
                    textViewSensorData.text = "Sensor Data: Heart Rate = $heartRate"
                }
            }
        }
    }

    override fun onDestroy() {
        Wearable.getDataClient(this).removeListener(this)
        super.onDestroy()
    }

    fun cambiarIntent(){
        val intent = Intent(this@MainActivity, GraficaActivity::class.java)
        startActivity(intent)
        finish()
    }
}
