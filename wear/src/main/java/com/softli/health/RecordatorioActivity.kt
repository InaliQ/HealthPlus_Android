package com.softli.health

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable

class RecordatorioActivity : ComponentActivity() {
    private var idRecordatorio: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recordatorio_activity)

        val message = intent.getStringExtra("recordatorio_message")
        idRecordatorio = intent.getIntExtra("id_recordatorio", -1)

        val tvRecordatorioMessage = findViewById<TextView>(R.id.tvRecordatorioMessage)
        tvRecordatorioMessage.text = message

        val btnFinalizar = findViewById<Button>(R.id.btnFinalizar)
        btnFinalizar.setOnClickListener {
            idRecordatorio?.let { id ->
                enviarConfirmacion(id)
            }
            val intent = Intent(this@RecordatorioActivity, GraficaActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun enviarConfirmacion(idRecordatorio: Int) {
        Wearable.getNodeClient(this).connectedNodes.addOnSuccessListener { nodes ->
            for (node in nodes) {
                val mensajeConfirmacion = "$idRecordatorio"
                Wearable.getMessageClient(this)
                    .sendMessage(node.id, "/confirmation", mensajeConfirmacion.toByteArray())
                    .addOnSuccessListener {
                        Log.d("RecordatorioActivity", "Confirmación enviada exitosamente: $mensajeConfirmacion")
                    }
                    .addOnFailureListener { e ->
                        Log.e("RecordatorioActivity", "Fallo al enviar la confirmación", e)
                    }
            }
        }
    }
}
