package com.softli.health.views.ui.alertas

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
import com.softli.health.R
import com.softli.health.views.ui.alertas.placeholder.PlaceholderContent
import com.softli.health.views.ui.pacientes.MyPacienteRecyclerViewAdapter
import com.softli.health.views.ui.pacientes.PacientesViewModel

/**
 * A fragment representing a list of Items.
 */
class AlertasFragment : Fragment() {
    private lateinit var alertasViewModel: AlertasViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyAlertasRecyclerViewAdapter

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
        val view = inflater.inflate(R.layout.fragment_item_list_alertas, container, false)

        recyclerView = view.findViewById(R.id.listAlertas)
        if (recyclerView == null) {
            Log.e("PacienteFragment", "RecyclerView no encontrado. Asegúrate de que el ID es correcto en el XML.")
        } else {
            recyclerView.layoutManager = LinearLayoutManager(context)

            alertasViewModel = ViewModelProvider(this).get(AlertasViewModel::class.java)

            alertasViewModel.alerta.observe(viewLifecycleOwner, Observer { alertas ->
                Log.d("PacienteFragment", "Actualizando la lista de pacientes: $alertas")
                adapter = MyAlertasRecyclerViewAdapter(alertas)
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
            AlertasFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}