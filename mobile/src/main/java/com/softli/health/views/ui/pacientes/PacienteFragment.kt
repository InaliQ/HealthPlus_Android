package com.softli.health.views.ui.pacientes

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.softli.health.R
import android.util.Log
import androidx.navigation.fragment.findNavController
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.softli.health.config.SessionManager

class PacienteFragment : Fragment(), DataClient.OnDataChangedListener {
    private lateinit var pacientesViewModel: PacientesViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyPacienteRecyclerViewAdapter
    lateinit var sessionManager: SessionManager

    private var columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
        mandarContadorReloj()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewPacientes)
        if (recyclerView == null) {
        } else {
            sessionManager = SessionManager(requireContext())
            recyclerView.layoutManager = LinearLayoutManager(context)

            pacientesViewModel = ViewModelProvider(this).get(PacientesViewModel::class.java)
            pacientesViewModel.pacientes.observe(viewLifecycleOwner, Observer { pacientes ->
                Log.d("PacienteFragment", "Actualizando la lista de pacientes: $pacientes")
                adapter = MyPacienteRecyclerViewAdapter(pacientes,
                    onWarningClick = { paciente ->
                        // Aquí realizamos la transición a AlertasFragment
                        sessionManager.savePaciente(paciente)
                        findNavController().navigate(R.id.action_pacienteFragment_to_alertasFragment)
                    },
                    onRecordatorioClick = { paciente ->
                        sessionManager.savePaciente(paciente)
                        findNavController().navigate(R.id.action_navigation_pacientes_to_navigation_recordatorio)
                    }
                )
                recyclerView.adapter = adapter
            })
        }

        return view
    }

    companion object {
        const val ARG_COLUMN_COUNT = "column-count"

        @JvmStatic
        fun newInstance(columnCount: Int) =
            PacienteFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }

    private fun mandarContadorReloj() {
        val dataMap = PutDataMapRequest.create("/count").run {
            dataMap.putInt("count", 10)
            asPutDataRequest()
        }
        Wearable.getDataClient(requireContext()).putDataItem(dataMap)
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val dataItem = event.dataItem
                if (dataItem.uri.path == "/count") {
                    val dataMap = DataMapItem.fromDataItem(dataItem).dataMap
                    Log.d("PacienteFragment", "Data received: ${dataMap.getInt("alerta")}")
                }
            }
        }
    }
}

