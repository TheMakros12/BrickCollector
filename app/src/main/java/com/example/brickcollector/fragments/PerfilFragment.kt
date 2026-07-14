package com.example.brickcollector.fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.brickcollector.R
import com.example.brickcollector.data.Usuario
import com.example.brickcollector.data.LegoResponse
import com.example.brickcollector.data.CalculadoraPrecios
import com.example.brickcollector.database.LegoApplication
import coil.load
import com.example.brickcollector.databinding.FragmentPerfilBinding
import kotlinx.coroutines.launch

class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!
    private var usuario: Usuario? = null

    companion object {
        private const val ARG_USUARIO = "usuario"

        fun newInstance(usuario: Usuario): PerfilFragment {
            val fragment = PerfilFragment()
            val bundle = Bundle()
            bundle.putSerializable(ARG_USUARIO, usuario)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        usuario = arguments?.getSerializable(ARG_USUARIO) as? Usuario
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usuario?.let {
            val nombreCompleto = it.nombre + " " + it.apellidos
            binding.tvInfoNombre.text = nombreCompleto
            binding.tvInfoEmail.text = it.email
        }

        cargarTotales()

        binding.btnBorrarLego.setOnClickListener {
            mostrarDialogoDelete()
        }

        binding.btnBorrarTodosLosLegos.setOnClickListener {
            mostrarDialogoDeleteAll()
        }
    }

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

    private fun cargarTotales() {
        lifecycleScope.launch {
            try {
                val totalSets = LegoApplication.database.legoDao().getTotalSets()
                val totalPiezas = LegoApplication.database.legoDao().getTotalPieces()
                val allLegos = LegoApplication.database.legoDao().getAllLegos()

                binding.tvTotalSetsInfo.text =  totalSets.toString()
                binding.tvTotalPiezasInfo.text = totalPiezas.toString()

                // Calculate total collection value
                val totalVal = allLegos.sumOf { CalculadoraPrecios.calcularPrecioDouble(it.num_parts) }
                binding.tvValorVitrinaInfo.text = String.format("%.2f€", totalVal)

                calcularEstadisticas(allLegos)
                mostrarSetMasGrande(allLegos)

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error cargando totales", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarSetMasGrande(sets: List<LegoResponse>) {
        val largestSet = sets.maxByOrNull { it.num_parts }
        if (largestSet != null) {
            binding.tvTituloLargestSet.visibility = View.VISIBLE
            binding.cardLargestSet.visibility = View.VISIBLE

            binding.ivLargestSetImg.load(largestSet.set_img_url)
            binding.tvLargestSetName.text = largestSet.name
            binding.tvLargestSetPieces.text = "${largestSet.num_parts} piezas"
            binding.tvLargestSetYear.text = "Año: ${largestSet.year}"
        } else {
            binding.tvTituloLargestSet.visibility = View.GONE
            binding.cardLargestSet.visibility = View.GONE
        }
    }

    private fun calcularEstadisticas(sets: List<LegoResponse>) {
        if (sets.isEmpty()) {
            binding.tvNoStats.visibility = View.VISIBLE
            binding.rowStat1.visibility = View.GONE
            binding.rowStat2.visibility = View.GONE
            binding.rowStat3.visibility = View.GONE
            return
        }

        binding.tvNoStats.visibility = View.GONE

        // Group by theme_id and sort by count descending
        val distribution = sets.groupBy { it.theme_id }
            .map { (themeId, list) -> themeId to list.size }
            .sortedByDescending { it.second }

        val totalSets = sets.size

        // Setup up to 3 rows
        val rows = listOf(
            Triple(binding.rowStat1, binding.tvStatName1, Pair(binding.tvStatCount1, binding.pbStat1)),
            Triple(binding.rowStat2, binding.tvStatName2, Pair(binding.tvStatCount2, binding.pbStat2)),
            Triple(binding.rowStat3, binding.tvStatName3, Pair(binding.tvStatCount3, binding.pbStat3))
        )

        for (i in 0 until 3) {
            val (rowLayout, tvName, countProgress) = rows[i]
            val (tvCount, pbStat) = countProgress

            if (i < distribution.size) {
                val (themeId, count) = distribution[i]
                val name = localThemesMap[themeId] ?: "Tema ($themeId)"

                rowLayout.visibility = View.VISIBLE
                tvName.text = name
                tvCount.text = if (count == 1) "1 set" else "$count sets"

                val percentage = (count.toFloat() / totalSets.toFloat() * 100).toInt()
                pbStat.progress = percentage
            } else {
                rowLayout.visibility = View.GONE
            }
        }
    }

    private fun mostrarDialogoDeleteAll() {
        lifecycleScope.launch {
            try {
                val totalSets = LegoApplication.database.legoDao().getTotalSets()

                if (totalSets > 0) {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Eliminar todos los sets")
                        .setMessage("Esta acción eliminará todos los legos guardados. ¿Deseas continuar?")
                        .setPositiveButton("Eliminar") { _, _ ->
                            lifecycleScope.launch {
                                val deleted = LegoApplication.database.legoDao().deleteAllSets()
                                Toast.makeText(requireContext(), "Se han eliminado $deleted sets", Toast.LENGTH_SHORT).show()
                                cargarTotales()
                            }
                        }
                        .setNegativeButton("Cancelar", null)
                        .setIcon(R.drawable.ic_lego)
                        .show()
                } else {
                    Toast.makeText(requireContext(), "No hay datos guardados!!!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error cargando totales", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarDialogoDelete() {
        lifecycleScope.launch {
            try {
                val totalSets = LegoApplication.database.legoDao().getTotalSets()

                if (totalSets > 0) {
                    val input = binding.etBorrarLego.text.toString().trim()
                    if (input.isEmpty()) {
                        Toast.makeText(requireContext(), "Debes introducir un ID!!!", Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                    val id = if (input.contains("-")) input else "$input-1"

                    AlertDialog.Builder(requireContext())
                        .setTitle("Eliminar set con Id: $id")
                        .setMessage("Esta acción eliminará el set $id. ¿Deseas continuar?")
                        .setPositiveButton("Eliminar") { _, _ ->
                            lifecycleScope.launch {
                                val deleted = LegoApplication.database.legoDao().deleteSetById(id)
                                Toast.makeText(requireContext(), "Se han eliminado $deleted sets", Toast.LENGTH_SHORT).show()
                                cargarTotales()
                            }
                        }
                        .setNegativeButton("Cancelar", null)
                        .setIcon(R.drawable.ic_lego)
                        .show()
                } else {
                    Toast.makeText(requireContext(), "No hay datos guardados!!!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error cargando totales", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
