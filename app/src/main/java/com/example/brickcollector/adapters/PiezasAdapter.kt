package com.example.brickcollector.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.brickcollector.R
import com.example.brickcollector.data.PiezaResponse
import com.example.brickcollector.data.PiezasResponse
import com.example.brickcollector.databinding.ItemPiezaLegoBinding
import com.squareup.picasso.Picasso

class PiezasAdapter(private val piezas: MutableList<PiezaResponse>) : RecyclerView.Adapter<PiezasAdapter.ViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_pieza_lego, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val pieza = piezas[position]

        val numPiezas = pieza.quantity.toString() + " pzs."
        with(holder) {
            binding.tvNumPieza.text = pieza.part.part_num
            binding.tvPiezasLegoSet.text = numPiezas

            Picasso.get()
                .load(pieza.part.part_img_url)
                .into(binding.imgItem)
        }
    }

    override fun getItemCount(): Int = piezas.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemPiezaLegoBinding.bind(view)
    }

    fun setItems(newPiezas: List<PiezaResponse>) {
        piezas.clear()
        piezas.addAll(newPiezas)
        notifyDataSetChanged()
    }
}
