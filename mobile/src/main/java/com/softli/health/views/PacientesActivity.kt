package com.softli.health.views

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.softli.health.R
import com.softli.health.apiservice.RecordatorioApiService
import com.softli.health.databinding.ActivityPacientesListBinding
import com.softli.health.models.Recordatorio
import com.softli.health.repositories.SessionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime

class PacientesActivity : AppCompatActivity(), MessageClient.OnMessageReceivedListener {
    val context: Context = this
    private lateinit var binding: ActivityPacientesListBinding
    lateinit var sessionRepository: SessionRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionRepository = SessionRepository(this)

        binding = ActivityPacientesListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navView: BottomNavigationView = binding.navView

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_pacientes_list) as NavHostFragment
        val navController = navHostFragment.findNavController()

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_registro, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        Wearable.getMessageClient(this).addListener(this)

        createNotificationChannel()
        startProcess()
    }

    private fun loadRecordatorios() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5042/api/Recordatorio/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(RecordatorioApiService::class.java)
        val call = apiService.getRecordatorio()

        call.enqueue(object : Callback<List<Recordatorio>> {
            override fun onResponse(
                call: Call<List<Recordatorio>>,
                response: Response<List<Recordatorio>>
            ) {
                if (response.isSuccessful) {
                    sessionRepository.cleanFechasRecordatorio()
                    val fechas = response.body()?.map { it.fechaInicio } ?: emptyList()
                    sessionRepository.saveFechasRecordatorio(fechas)
                } else {
                    Log.e("RecordatorioViewModel", "Error en la respuesta: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Recordatorio>>, t: Throwable) {
                Log.e("RecordatorioViewModel", "Error en la llamada a la API", t)
            }
        })
    }

    private fun startCheckingDateTime(
        fechas: List<String>,
        onTimeReached: () -> Unit
    ) {
        CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                val now = LocalDateTime.now()

                for (fecha in fechas) {
                    try {
                        val targetDateTime = LocalDateTime.parse(fecha)
                        // Comprobar si la fecha objetivo está dentro del rango de 5 minutos
                        if ((targetDateTime.isAfter(now) && targetDateTime.isBefore(
                                now.plusSeconds(
                                    30
                                )
                            ) || targetDateTime == now)
                        ) {
                            onTimeReached()
                        }
                    } catch (e: Exception) {
                        Log.e("startCheckingDateTime", "Error al parsear la fecha: $fecha", e)
                    }
                }

                // Esperar un minuto antes de volver a comprobar
                delay(5000)
            }
        }
    }

    fun startProcess() {
        // Carga los recordatorios inicialmente
        loadRecordatorios()

        // Cargar fechas desde el sessionRepository
        val fechas = sessionRepository.getFechasRecordatorio() ?: emptyList()

        // Comienza la verificación
        startCheckingDateTime(fechas) {
            // Aquí se ejecuta el código cuando la fecha coincide
            Log.d("startCheckingDateTime", "¡Fecha y hora objetivo alcanzadas!")
            requestNotificationPermission()
        }
    }


    private fun requestNotificationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_CODE_POST_NOTIFICATIONS
            )
        } else {
            showNotification("Título de la Notificación", "Mensaje de la Notificación")
        }
    }

    private fun createNotificationChannel() {
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

    private fun showNotification(title: String, message: String) {
        val intent = Intent(context, PacientesActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)


        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    companion object {
        private const val CHANNEL_ID = "canal_de_notificaciones"
        private const val NOTIFICATION_ID = 1
        private const val REQUEST_CODE_POST_NOTIFICATIONS = 1001
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.path == "/hear_rate") {
            val message = String(messageEvent.data)
            actualizarHeartRate(message)
            Log.d("HearRate", "Message received: $message")
        }
        if (messageEvent.path == "/emergency_status") {
            val message = String(messageEvent.data)
            actualizarHeartRate(message)
            Log.d("Emergencia", "Message received: $message")
        }
        if (messageEvent.path == "/confirmation") {
            val message = String(messageEvent.data)
            actualizarHeartRate(message)
            Log.d("Recordatorio", "Message received: $message")
        }
    }


    private fun actualizarHeartRate(message: String) {
        Log.d("PacientesActivity", "Mensaje recibido: $message")
    }

    override fun onDestroy() {
        super.onDestroy()
        Wearable.getMessageClient(this).removeListener(this)
    }


}

