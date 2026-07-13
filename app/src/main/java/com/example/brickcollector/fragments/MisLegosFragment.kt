package com.example.brickcollector.fragments

import android.graphics.Color
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
import com.example.brickcollector.adapters.LegosGuardadosAdapter
import com.example.brickcollector.api.RetrofitInstance
import com.example.brickcollector.data.LegoResponse
import com.example.brickcollector.database.LegoApplication
import com.example.brickcollector.databinding.FragmentMisLegosBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MisLegosFragment : Fragment() {

    private lateinit var binding: FragmentMisLegosBinding
    private var themesMap: Map<Int, String> = emptyMap()
    private lateinit var spinnerItems: List<String>
    private lateinit var themeIdsOrdered: List<Int>
    private lateinit var adapter: LegosGuardadosAdapter
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
        binding = FragmentMisLegosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recylerViewLegos.layoutManager = LinearLayoutManager(requireContext())
        cargarThemesYSpinner()
    }

    private fun cargarThemesYSpinner(themeIdToSelect: Int? = null) {
        lifecycleScope.launch {
            try {
                val categoriasGuardadas =
                    LegoApplication.database.legoDao().getSavedThemeIds()

                if (categoriasGuardadas.isEmpty()) {
                    Toast.makeText(requireContext(), "No tienes legos guardados", Toast.LENGTH_SHORT).show()
                    if (::adapter.isInitialized) {
                        adapter.setItems(emptyList())
                    }
                    binding.spinnerCategorias.adapter = null
                    return@launch
                }

                val categoriasUnicas = categoriasGuardadas.distinct()

                if (themesMap.isEmpty()) {
                    val themesResponse = RetrofitInstance.api.getThemes(limit = 1000)
                    themesMap = themesResponse.results.associate { it.id to it.name }
                }

                themeIdsOrdered = categoriasUnicas
                spinnerItems = themeIdsOrdered.map { id ->
                    themesMap[id] ?: localThemesMap[id] ?: "Tema desconodico ($id)"
                }

                if (!::adapter.isInitialized) {
                    adapter = LegosGuardadosAdapter(
                        mutableListOf(),
                        themesMap,
                        onBorrarClick = { legoResponse ->
                            mostrarDialogoConfirmacion(legoResponse)
                        },
                        onItemClick = { legoResponse ->
                            abrirPiezasFragment(legoResponse.set_num)
                        }
                    )
                    binding.recylerViewLegos.adapter = adapter
                }

                cargarSpinner()
                setupSpinnerListener()

                val indexToSelect = themeIdToSelect?.let { themeIdsOrdered.indexOf(it) } ?: -1
                if (indexToSelect != -1) {
                    binding.spinnerCategorias.setSelection(indexToSelect)
                }

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error cargando categorías", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cargarSpinner() {
        val adapterSpinner = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            spinnerItems
        )
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategorias.adapter = adapterSpinner
    }

    private fun setupSpinnerListener() {
        binding.spinnerCategorias.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val themeId = themeIdsOrdered[position]

                    lifecycleScope.launch {
                        val legos = LegoApplication.database.legoDao().getLegosByTheme(themeId)
                        adapter.setItems(legos)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    private fun mostrarDialogoConfirmacion(legoResponse: LegoResponse) {
        com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
            .setTitle("Confirmar Eliminación")
            .setMessage("Esta acción eliminará el elemento de manera permanente. ¿Desea continuar?")
            .setPositiveButton("Eliminar") { dialog, _ ->
                lifecycleScope.launch {
                    try {
                        LegoApplication.database.legoDao().deleteSet(legoResponse)
                        adapter.removeItem(legoResponse)

                        val remainingSetsOfTheme = LegoApplication.database.legoDao().getLegosByTheme(legoResponse.theme_id)
                        if (remainingSetsOfTheme.isEmpty()) {
                            cargarThemesYSpinner()
                        }

                        Snackbar.make(binding.root, "Elemento eliminado.", Snackbar.LENGTH_LONG)
                            .setAction("Deshacer") {
                                lifecycleScope.launch {
                                    try {
                                        LegoApplication.database.legoDao().insertSet(legoResponse)

                                        if (!::themeIdsOrdered.isInitialized || !themeIdsOrdered.contains(legoResponse.theme_id)) {
                                            cargarThemesYSpinner(legoResponse.theme_id)
                                        } else {
                                            val currentSelectedPosition = binding.spinnerCategorias.selectedItemPosition
                                            val currentSelectedThemeId = if (currentSelectedPosition >= 0 && currentSelectedPosition < themeIdsOrdered.size) {
                                                themeIdsOrdered[currentSelectedPosition]
                                            } else {
                                                null
                                            }

                                            if (currentSelectedThemeId == legoResponse.theme_id) {
                                                adapter.addItem(legoResponse)
                                            } else {
                                                val index = themeIdsOrdered.indexOf(legoResponse.theme_id)
                                                if (index != -1) {
                                                    binding.spinnerCategorias.setSelection(index)
                                                }
                                            }
                                        }
                                        Toast.makeText(requireContext(), "Acción deshecha", Toast.LENGTH_SHORT).show()
                                    } catch (e: Exception) {
                                        Toast.makeText(requireContext(), "No se pudo deshacer", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                            .setActionTextColor(Color.GRAY)
                            .show()

                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "ERROR!!! No se ha borrado", Toast.LENGTH_SHORT).show()
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .setIcon(R.drawable.ic_lego)
            .show()
    }

    private fun abrirPiezasFragment(setNum: String) {
        val fragment = PiezasFragment.newInstance(setNum)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }
}
