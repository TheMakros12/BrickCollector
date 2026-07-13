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
import com.example.brickcollector.databinding.FragmentDetalleLegoBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.squareup.picasso.Picasso
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
                val lego = RetrofitInstance.api.getLegoById(setNum)
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

        val precio = CalculadoraPrecios.calcularPrecio(lego.num_parts)
        binding.tvPrecioEstimadoInfo.text = precio

        Picasso.get()
            .load(lego.set_img_url)
            .into(binding.ivImgLego)
    }

    private fun abrirWeb() {
        val urlLego = legoActual.set_url
        dismiss()
        (activity as? NavigationWebListener)?.abrirWebFragment(urlLego)
    }
}
