package com.softli.health

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.ComponentActivity
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable

class RecordatorioActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recordatorio_activity)

        val btnFinalizar = findViewById<Button>(R.id.btnFinalizar)

        btnFinalizar.setOnClickListener {
            enviarConfirmacion()
        }
    }

    private fun enviarConfirmacion() {
        val dataMap = PutDataMapRequest.create("/confirmation").run {
            dataMap.putString("mensaje", "Recordatorio atendido")
            asPutDataRequest()
        }
        Wearable.getDataClient(this).putDataItem(dataMap)
            .addOnSuccessListener {
                Log.d("RecordatorioActivity", "Confirmación enviada exitosamente")
            }
            .addOnFailureListener { e ->
                Log.e("RecordatorioActivity", "Fallo al enviar la confirmación", e)
            }
    }
}
