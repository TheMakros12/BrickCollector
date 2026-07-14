package com.example.brickcollector.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.brickcollector.R
import com.example.brickcollector.adapters.PiezasAdapter
import com.example.brickcollector.api.RetrofitInstance
import com.example.brickcollector.databinding.FragmentPiezasBinding
import kotlinx.coroutines.launch

class PiezasFragment : Fragment() {

    private var _binding: FragmentPiezasBinding? = null
    private val binding get() = _binding!!
    private lateinit var setNum: String
    private lateinit var adapter: PiezasAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setNum = requireArguments().getString(ARG_SET_NUM)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPiezasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        adapter = PiezasAdapter(mutableListOf())
        binding.recyclerView.adapter = adapter

        cargarPiezas()
    }

    private fun cargarPiezas() {
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.getPiezas(setNum)
                adapter.setItems(response.results)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error cargando piezas: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val ARG_SET_NUM = "set_num"

        fun newInstance(setNum: String): PiezasFragment {
            return PiezasFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_SET_NUM, setNum)
                }
            }
        }
    }
}
