package com.example.brickcollector.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.brickcollector.R
import com.example.brickcollector.data.LegoResponse
import com.example.brickcollector.databinding.ItemLegoBuscarBinding
import com.squareup.picasso.Picasso

class LegoAdapter(private val legos: MutableList<LegoResponse>,
                  private val themes: Map<Int, String>,
                  private val onGuardarClick: (LegoResponse) -> Unit,
                  private val onItemClick: (LegoResponse) -> Unit
): RecyclerView.Adapter<LegoAdapter.ViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_lego_buscar, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val lego = legos[position]
        val legoId = lego.set_num.split("-")[0]
        val legoPiezas = lego.num_parts.toString() + " pzs."

        with(holder) {
            binding.tvIdLego.text = legoId
            binding.tvNombreLego.text = lego.name
            binding.tvCategoriaLego.text = themes[lego.theme_id]
            binding.tvPiezasLego.text = legoPiezas
            Picasso.get()
                .load(lego.set_img_url)
                .into(binding.ivLego)

            binding.btnGuardarLego.setOnClickListener {
                onGuardarClick(lego)
            }

            itemView.setOnClickListener {
                onItemClick(lego)
            }
        }
    }

    override fun getItemCount(): Int = legos.size


    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding = ItemLegoBuscarBinding.bind(view)
    }

    fun setItems(nuevosLegos: List<LegoResponse>) {
        legos.clear()
        legos.addAll(nuevosLegos)
        notifyDataSetChanged()
    }

}
