package com.softli.health.views.ui.pacientes

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.softli.health.R
import com.softli.health.apiservice.PacienteApiService
import com.softli.health.apiservice.RetrofitClientPaciente
import com.softli.health.models.Paciente
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class PacienteRegistroFragment : Fragment() {
    lateinit var nombreInputLayout: TextInputLayout
    lateinit var nombreEditText: TextInputEditText
    lateinit var primerApellidoInputLayout: TextInputLayout
    lateinit var primerApellidoEditText: TextInputEditText
    lateinit var segundoApellidoInputLayout: TextInputLayout
    lateinit var segundoApellidoEditText: TextInputEditText
    lateinit var telefonoInputLayout: TextInputLayout
    lateinit var telefonoEditText: TextInputEditText
    lateinit var calleInputLayout: TextInputLayout
    lateinit var calleEditText: TextInputEditText
    lateinit var numeroInputLayout: TextInputLayout
    lateinit var numeroEditText: TextInputEditText
    lateinit var codigoPostalInputLayout: TextInputLayout
    lateinit var codigoPostalEditText: TextInputEditText
    lateinit var coloniaInputLayout: TextInputLayout
    lateinit var coloniaEditText: TextInputEditText
    lateinit var alturaInputLayout: TextInputLayout
    lateinit var alturaEditText: TextInputEditText
    lateinit var pesoInputLayout: TextInputLayout
    lateinit var pesoEditText: TextInputEditText
    lateinit var tipoDeSangreInputLayout: TextInputLayout
    lateinit var tipoDeSangreEditText: TextInputEditText
    lateinit var contexto: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contexto = requireContext()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_paciente_registro, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nombreInputLayout = view.findViewById(R.id.nombreInputLayout)
        nombreEditText = view.findViewById(R.id.nombreEditText)
        primerApellidoInputLayout = view.findViewById(R.id.primerApellidoInputLayout)
        primerApellidoEditText = view.findViewById(R.id.primerApellidoEditText)
        segundoApellidoInputLayout = view.findViewById(R.id.segundoApellidoInputLayout)
        segundoApellidoEditText = view.findViewById(R.id.segundoApellidoEditText)
        telefonoInputLayout = view.findViewById(R.id.telefonoInputLayout)
        telefonoEditText = view.findViewById(R.id.telefonoEditText)
        calleInputLayout = view.findViewById(R.id.calleInputLayout)
        calleEditText = view.findViewById(R.id.calleEditText)
        numeroInputLayout = view.findViewById(R.id.numeroInputLayout)
        numeroEditText = view.findViewById(R.id.numeroEditText)
        codigoPostalInputLayout = view.findViewById(R.id.codigoPostalInputLayout)
        codigoPostalEditText = view.findViewById(R.id.codigoPostalEditText)
        coloniaInputLayout = view.findViewById(R.id.coloniaInputLayout)
        coloniaEditText = view.findViewById(R.id.coloniaEditText)
        alturaInputLayout = view.findViewById(R.id.alturaInputLayout)
        alturaEditText = view.findViewById(R.id.alturaEditText)
        pesoInputLayout = view.findViewById(R.id.pesoInputLayout)
        pesoEditText = view.findViewById(R.id.pesoEditText)
        tipoDeSangreInputLayout = view.findViewById(R.id.tipoDeSangreInputLayout)
        tipoDeSangreEditText = view.findViewById(R.id.tipoDeSangreEditText)

        val registrarButton = view.findViewById<Button>(R.id.registrarButton)
        registrarButton.setOnClickListener {
            if (formularioValido()) {
                val paciente = Paciente(
                    0,
                    generarNumPaciente(nombreEditText.text.toString()),
                    alturaEditText.text.toString(),
                    pesoEditText.text.toString(),
                    tipoDeSangreEditText.text.toString(),
                    true,
                    0,
                    nombreEditText.text.toString(),
                    primerApellidoEditText.text.toString(),
                    segundoApellidoEditText.text.toString(),
                    telefonoEditText.text.toString(),
                    calleEditText.text.toString(),
                    numeroEditText.text.toString(),
                    codigoPostalEditText.text.toString(),
                    coloniaEditText.text.toString()
                )
                registro(paciente)
            } else {
                Toast.makeText(contexto, "Formulario no valido", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun registro(paciente: Paciente) {
        RetrofitClientPaciente.instance.agregarPaciente(paciente)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(contexto, "Paciente registrado", Toast.LENGTH_SHORT).show()
                        reiniciarFormulario()
                    } else {
                        Toast.makeText(contexto, "Error al registrar paciente", Toast.LENGTH_SHORT)
                            .show()
                        Log.e("PacienteApiService", "Error en la respuesta: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(contexto, "Error al registrar paciente: ${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

    fun generarNumPaciente(nombre: String): String {
        val now = java.util.Date()
        val horas = now.hours.toString().padStart(2, '0')
        val min = now.minutes.toString().padStart(2, '0')
        val seg = now.seconds.toString().padStart(2, '0')
        val primerasDos = nombre.substring(0, 2).uppercase()
        val ultima = nombre.substring(nombre.length - 1).uppercase()

        return "P${primerasDos}${horas}${min}${seg}${ultima}"
    }

    fun reiniciarFormulario() {
        nombreEditText.text?.clear()
        primerApellidoEditText.text?.clear()
        segundoApellidoEditText.text?.clear()
        telefonoEditText.text?.clear()
        calleEditText.text?.clear()
        numeroEditText.text?.clear()
        codigoPostalEditText.text?.clear()
        coloniaEditText.text?.clear()
        alturaEditText.text?.clear()
        pesoEditText.text?.clear()
        tipoDeSangreEditText.text?.clear()
    }

    fun formularioValido(): Boolean {
        var valido = true
        if (nombreEditText.text.toString().isEmpty()) {
            nombreInputLayout.error = "Campo obligatorio"
            valido = false
        } else {
            nombreInputLayout.error = ""
        }
        if (primerApellidoEditText.text.toString().isEmpty()) {
            primerApellidoInputLayout.error = "Campo obligatorio"
            valido = false
        } else {
            primerApellidoInputLayout.error = ""
        }
        if (segundoApellidoEditText.text.toString().isEmpty()) {
            segundoApellidoInputLayout.error = "Campo obligatorio"
            valido = false
        } else {
            segundoApellidoInputLayout.error = ""
        }
        if (telefonoEditText.text.toString().isEmpty()) {
            telefonoInputLayout.error = "Campo obligatorio"
            valido = false
        } else {
            telefonoInputLayout.error = ""
        }
        if (calleEditText.text.toString().isEmpty()) {
            calleInputLayout.error = "Campo obligatorio"
            valido = false
        } else {
            calleInputLayout.error = ""
        }
        if (numeroEditText.text.toString().isEmpty()) {
            numeroInputLayout.error = "Campo obligatorio"
            valido = false
        } else {
            numeroInputLayout.error = ""
        }
        if (codigoPostalEditText.text.toString().isEmpty()) {
            codigoPostalInputLayout.error = "Campo obligatorio"
            valido = false
        } else {
            codigoPostalInputLayout.error = ""
        }
        if (coloniaEditText.text.toString().isEmpty()) {
            coloniaInputLayout.error = "Campo obligatorio"
            valido = false
        } else {
            coloniaInputLayout.error = ""
        }
        if (alturaEditText.text.toString().isEmpty()) {
            alturaInputLayout.error = "Campo obligatorio"
            valido = false
        } else {
            alturaInputLayout.error = ""
        }
        if (pesoEditText.text.toString().isEmpty()) {
            pesoInputLayout.error = "Campo obligatorio"
            valido = false
        } else {
            pesoInputLayout.error = ""
        }
        if (tipoDeSangreEditText.text.toString().isEmpty()) {
            tipoDeSangreInputLayout.error = "Campo obligatorio"
            valido = false
        } else {
            tipoDeSangreInputLayout.error = ""
        }
        return valido
    }

}