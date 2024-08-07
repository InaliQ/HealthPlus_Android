package com.softli.health.views.ui.recordatorio

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.softli.health.R
import com.softli.health.views.ui.recordatorio.placeholder.PlaceholderContent

/**
 * A fragment representing a list of Items.
 */
class RecordatorioFragment : Fragment(), DataClient.OnDataChangedListener {
    private lateinit var recordatorioViewModel: RecordatorioViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyRecordatorioRecyclerViewAdapter

    private var columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_recordatorio_list, container, false)

        recyclerView = view.findViewById(R.id.list)
        if (recyclerView == null) {
            Log.e("PacienteFragment", "RecyclerView no encontrado. Asegúrate de que el ID es correcto en el XML.")
        } else {
            recyclerView.layoutManager = LinearLayoutManager(context)

            recordatorioViewModel = ViewModelProvider(this).get(RecordatorioViewModel::class.java)
            recordatorioViewModel.recordatorios.observe(viewLifecycleOwner, Observer { recordatorios ->
                Log.d("PacienteFragment", "Actualizando la lista de pacientes: $recordatorios")
                adapter = MyRecordatorioRecyclerViewAdapter(recordatorios)
                recyclerView.adapter = adapter
            })
        }
        return view
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            RecordatorioFragment().apply {
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