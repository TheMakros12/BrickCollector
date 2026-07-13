package com.example.brickcollector.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.brickcollector.R
import com.example.brickcollector.adapters.LegoAdapter
import com.example.brickcollector.api.RetrofitInstance
import com.example.brickcollector.data.LegoResponse
import com.example.brickcollector.database.AppDatabase
import com.example.brickcollector.database.LegoApplication
import com.example.brickcollector.databinding.FragmentBuscarLegosBinding
import kotlinx.coroutines.launch

class BuscarLegosFragment : Fragment() {

    private lateinit var binding: FragmentBuscarLegosBinding
    private lateinit var legoAdapter: LegoAdapter
    private val categorias = arrayOf("Technic", "Speed Champions", "Icons", "Star Wars", "Botanicals", "Marvel", "Nike", "The Infinity Saga", "Pokemon")
    private lateinit var themes: Map<Int, String>
    private val themesId = mapOf(
        "Technic" to 1,
        "Speed Champions" to 601,
        "Icons" to 721,
        "Star Wars" to 171,
        "Botanicals" to 769,
        "Marvel" to 702,
        "Nike" to 785,
        "The Infinity Saga" to 781,
        "Pokemon" to 776
    )

    private val localThemesMap = mapOf(
        1 to "Technic",
        601 to "Speed Champions",
        721 to "Icons",
        171 to "Star Wars",
        769 to "Botanicals",
        702 to "Marvel",
        785 to "Nike",
        781 to "The Infinity Saga",
        776 to "Pokemon"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBuscarLegosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recylerViewLegos.layoutManager = LinearLayoutManager(requireContext())

        cargarThemes {
            setupRecyclerView()
            cargarSpinner()
            setupSpinnerListener()
            cargarSetsIniciales()
        }

        binding.btnBuscarLego.setOnClickListener {
            val input = binding.tiIdLego.text.toString().trim()

            if (input.isEmpty()) {
                Toast.makeText(requireContext(), "Debes introducir un ID!!!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val idLego = if (input.contains("-")) input else "$input-1"

            buscarYGuardarSet(idLego)
        }
    }

    private fun setupRecyclerView() {
        legoAdapter = LegoAdapter(
            mutableListOf(),
            themes,
            onGuardarClick = { lego ->
                guardarLego(lego)
            },
            onItemClick = { lego ->
                mostrarDetalleLego(lego)
            })

        binding.recylerViewLegos.adapter = legoAdapter
    }

    private fun cargarSpinner() {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categorias)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategorias.adapter = adapter
    }

    private fun cargarThemes(onLoaded: () -> Unit) {
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.getThemes()
                val apiThemes = response.results.associate { it.id to it.name }
                themes = apiThemes + localThemesMap
                onLoaded()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error cargando categorías", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cargarSetsIniciales() {
        val firstThemeId = themesId[categorias[0]] ?: return

        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.getSets(firstThemeId)
                legoAdapter.setItems(response.results)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error cargando sets", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSpinnerListener() {
        binding.spinnerCategorias.onItemSelectedListener =
            object : android.widget.AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selected = categorias[position]
                    val themeId = themesId[selected] ?: return

                    lifecycleScope.launch {
                        try {
                            val response = RetrofitInstance.api.getSets(themeId)
                            legoAdapter.setItems(response.results)
                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), "Error en la API", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
            }
    }

    private fun guardarLego(legoResponse: LegoResponse) {
        lifecycleScope.launch {
            try {
                val resultado = LegoApplication.database.legoDao().insertSet(legoResponse)

                if (resultado > 0) {
                    Toast.makeText(requireContext(), "Has guardado el Lego ${legoResponse.set_num.split("-")[0]}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Este Lego ya estaba guardado", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error guardando el Lego", Toast.LENGTH_SHORT).show()
                Log.e("Error Guardado", "$e")
            }
        }
    }

    private fun mostrarDetalleLego(lego: LegoResponse) {
        val bottomSheet = DetalleLegoFragment.newInstance(lego.set_num)
        bottomSheet.show(parentFragmentManager, "LegoDetailBottomSheet")
    }

    private fun buscarYGuardarSet(idLego: String) {
        lifecycleScope.launch {
            try {
                val lego = RetrofitInstance.api.getLegoById(idLego)
                val resultado = LegoApplication.database.legoDao().insertSet(lego)

                if ( resultado > 0 ) {
                    Toast.makeText(requireContext(), "Set ${lego.set_num} guardado correctamente", Toast.LENGTH_SHORT).show()
                }else {
                    Toast.makeText(requireContext(), "Este set ya estaba guardado", Toast.LENGTH_SHORT).show()
                }
            } catch (e: retrofit2.HttpException) {
                if (e.code() == 404) {
                    Toast.makeText(requireContext(), "No existe ningún set con ese ID", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Error en la API (${e.code()})", Toast.LENGTH_SHORT).show()
                }
                Log.e("BuscarSet", "Error HTTP", e)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error inesperado", Toast.LENGTH_SHORT).show()
                Log.e("BuscarSet", "Error", e)
            }
        }
    }
}
