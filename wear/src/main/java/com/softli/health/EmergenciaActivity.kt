package com.softli.health

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.softli.health.R

class EmergenciaActivity : ComponentActivity() {

    private val REQUEST_CALL_PERMISSION = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.emergencia_activity)

        val btnEmergencia = findViewById<ImageButton>(R.id.btnEmergencia)

        // Configurar el evento onClick para el botón
        btnEmergencia.setOnClickListener {
            enviarMensajeDeEmergencia()
            realizarLlamadaDeEmergencia()
            regresarAGraficaActivity()
        }
    }

    private fun enviarMensajeDeEmergencia() {
        Wearable.getNodeClient(this).connectedNodes.addOnSuccessListener { nodes ->
            for (node in nodes) {
                Wearable.getMessageClient(this).sendMessage(node.id, "/emergency_status", "Emergencia Pendiente".toByteArray())
                    .addOnSuccessListener {
                        Log.d("EmergenciaActivity", "Mensaje de emergencia enviado exitosamente")
                    }.addOnFailureListener { e ->
                        Log.e("EmergenciaActivity", "Fallo al enviar el mensaje de emergencia", e)
                    }
            }
        }
    }


    private fun realizarLlamadaDeEmergencia() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            val telefono = "tel:4777343569"
            val intent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse(telefono)
            }

            startActivity(intent)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), REQUEST_CALL_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CALL_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                realizarLlamadaDeEmergencia()
            } else {
                Log.e("EmergenciaActivity", "Permiso de llamada no concedido")
            }
        }
    }

    private fun regresarAGraficaActivity() {
        val intent = Intent(this, GraficaActivity::class.java)
        startActivity(intent)
        finish()
    }
}