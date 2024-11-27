package com.softli.health

import android.content.Intent
import android.os.Bundle
import android.os.Vibrator
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class InfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.info_activity) // Asegúrate de que el layout sea el correcto

        vibrarTelefono()
        val btnAtendido = findViewById<Button>(R.id.btnAtendido)
        btnAtendido.setOnClickListener {
            val intent = Intent(this, InicioActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun vibrarTelefono() {
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator

        if (vibrator.hasVibrator()) {
            val patron = longArrayOf(0, 500, 200, 500) // 0ms de espera, luego 500ms de vibración, 200ms de pausa, y otra vibración de 500ms.
            vibrator.vibrate(patron, 0) // El último parámetro (0) es el índice de repetición del patrón
        }
    }
}
