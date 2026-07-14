package com.example.brickcollector.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.brickcollector.activities.NavigationWebListener
import com.example.brickcollector.api.RetrofitInstance
import com.example.brickcollector.data.CalculadoraPrecios
import com.example.brickcollector.data.LegoResponse
import com.example.brickcollector.database.LegoApplication
import com.example.brickcollector.databinding.FragmentDetalleLegoBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import coil.load
import kotlinx.coroutines.launch

class DetalleLegoFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentDetalleLegoBinding
    private lateinit var legoActual: LegoResponse

    companion object {
        private const val ARG_SET_NUM = "set_num"

        fun newInstance(setNum: String): DetalleLegoFragment {
            return DetalleLegoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_SET_NUM, setNum)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetalleLegoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val setNum = arguments?.getString(ARG_SET_NUM) ?: return
        cargarDetalle(setNum)

        binding.btnPaginaWeb.setOnClickListener {
            abrirWeb()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.findViewById<View>(
            com.google.android.material.R.id.design_bottom_sheet
        )?.let {
            BottomSheetBehavior.from(it).state =
                BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun cargarDetalle(setNum: String) {
        lifecycleScope.launch {
            try {
                var lego = LegoApplication.database.legoDao().getLegoByNum(setNum)
                if (lego == null) {
                    val apiLego = RetrofitInstance.api.getLegoById(setNum)
                    val price = obtenerPrecioRealBrickset(setNum) ?: CalculadoraPrecios.calcularPrecioDouble(apiLego.num_parts)
                    lego = apiLego.copy(retail_price = price)
                }
                bindLego(lego)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error cargando detalle", Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }
    }

    private fun bindLego(lego: LegoResponse) {
        legoActual = lego

        binding.tvIdLego.text = lego.set_num.split("-")[0]
        binding.tvNombreLego.text = lego.name

        binding.tvPiezasLego.text = "${lego.num_parts} pzs."
        binding.tvAnyoSalidaInfo.text = lego.year.toString()

        val price = lego.retail_price ?: CalculadoraPrecios.calcularPrecioDouble(lego.num_parts)
        binding.tvPrecioEstimadoInfo.text = String.format("%.2f€", price)

        binding.ivImgLego.load(lego.set_img_url)

        if (!lego.isWishlist) {
            binding.cardBuildTracker.visibility = View.VISIBLE
            binding.switchBuilding.isChecked = lego.isBuilding
            binding.layoutBuildProgress.visibility = if (lego.isBuilding) View.VISIBLE else View.GONE
            
            binding.tvCurrentBag.text = lego.currentBag.toString()
            binding.etTotalBags.setText(if (lego.totalBags > 0) lego.totalBags.toString() else "")

            binding.switchBuilding.setOnCheckedChangeListener { _, isChecked ->
                binding.layoutBuildProgress.visibility = if (isChecked) View.VISIBLE else View.GONE
                actualizarBuildProgress(isChecked, lego.currentBag, binding.etTotalBags.text.toString().toIntOrNull() ?: lego.totalBags)
            }

            binding.btnBagPlus.setOnClickListener {
                val total = binding.etTotalBags.text.toString().toIntOrNull() ?: 0
                val current = binding.tvCurrentBag.text.toString().toInt()
                if (total == 0 || current < total) {
                    val newCurrent = current + 1
                    binding.tvCurrentBag.text = newCurrent.toString()
                    actualizarBuildProgress(binding.switchBuilding.isChecked, newCurrent, total)
                }
            }

            binding.btnBagMinus.setOnClickListener {
                val total = binding.etTotalBags.text.toString().toIntOrNull() ?: 0
                val current = binding.tvCurrentBag.text.toString().toInt()
                if (current > 0) {
                    val newCurrent = current - 1
                    binding.tvCurrentBag.text = newCurrent.toString()
                    actualizarBuildProgress(binding.switchBuilding.isChecked, newCurrent, total)
                }
            }
            
            binding.etTotalBags.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    val total = binding.etTotalBags.text.toString().toIntOrNull() ?: 0
                    val current = binding.tvCurrentBag.text.toString().toInt()
                    actualizarBuildProgress(binding.switchBuilding.isChecked, current, total)
                }
            }

            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            
            if (lego.startDate != null) {
                binding.layoutBuildDates.visibility = View.VISIBLE
                binding.tvStartDate.text = "Iniciado el: ${dateFormat.format(Date(lego.startDate))}"
                
                if (lego.endDate != null) {
                    binding.tvEndDate.visibility = View.VISIBLE
                    val diff = lego.endDate - lego.startDate
                    val days = (diff / (1000 * 60 * 60 * 24)).toInt()
                    val daysText = if (days == 0) "en el mismo día" else "en $days días"
                    binding.tvEndDate.text = "Finalizado el: ${dateFormat.format(Date(lego.endDate))} ($daysText)"
                } else {
                    binding.tvEndDate.visibility = View.GONE
                }
            } else {
                binding.layoutBuildDates.visibility = View.GONE
            }
        } else {
            binding.cardBuildTracker.visibility = View.GONE
        }
    }

    private suspend fun obtenerPrecioRealBrickset(setNum: String): Double? {
        return try {
            val paramsJson = "{\"setNumber\":\"$setNum\"}"
            val response = RetrofitInstance.bricksetApi.getSets(
                apiKey = RetrofitInstance.BRICKSET_API_KEY,
                params = paramsJson
            )
            if (response.status == "success" && response.matches > 0) {
                response.sets?.firstOrNull()?.LEGOCom?.DE?.retailPrice
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("BricksetPrice", "Error fetching price from Brickset for $setNum", e)
            null
        }
    }

    private fun abrirWeb() {
        val urlLego = legoActual.set_url
        dismiss()
        (activity as? NavigationWebListener)?.abrirWebFragment(urlLego)
    }

    private fun actualizarBuildProgress(isBuilding: Boolean, currentBag: Int, totalBags: Int) {
        lifecycleScope.launch {
            try {
                var newStartDate = legoActual.startDate
                var newEndDate = legoActual.endDate

                if (isBuilding && newStartDate == null) {
                    newStartDate = System.currentTimeMillis()
                }

                if (totalBags > 0 && currentBag == totalBags) {
                    if (newEndDate == null) newEndDate = System.currentTimeMillis()
                } else {
                    newEndDate = null // Reset si el usuario vuelve atrás
                }

                LegoApplication.database.legoDao().updateBuildProgress(legoActual.set_num, isBuilding, currentBag, totalBags, newStartDate, newEndDate)
                legoActual = legoActual.copy(isBuilding = isBuilding, currentBag = currentBag, totalBags = totalBags, startDate = newStartDate, endDate = newEndDate)
                
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                if (newStartDate != null) {
                    binding.layoutBuildDates.visibility = View.VISIBLE
                    binding.tvStartDate.text = "Iniciado el: ${dateFormat.format(Date(newStartDate))}"
                    
                    if (newEndDate != null) {
                        binding.tvEndDate.visibility = View.VISIBLE
                        val diff = newEndDate - newStartDate
                        val days = (diff / (1000 * 60 * 60 * 24)).toInt()
                        val daysText = if (days == 0) "en el mismo día" else "en $days días"
                        binding.tvEndDate.text = "Finalizado el: ${dateFormat.format(Date(newEndDate))} ($daysText)"
                    } else {
                        binding.tvEndDate.visibility = View.GONE
                    }
                } else {
                    binding.layoutBuildDates.visibility = View.GONE
                }
            } catch (e: Exception) {
                Log.e("DetalleLego", "Error actualizando build progress", e)
            }
        }
    }
}
