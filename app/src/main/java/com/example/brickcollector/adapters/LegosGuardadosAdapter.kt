package com.example.brickcollector.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.brickcollector.R
import com.example.brickcollector.data.LegoResponse
import com.example.brickcollector.databinding.ItemLegoGuardadoBinding
import coil.load

class LegosGuardadosAdapter(private val legos: MutableList<LegoResponse>,
                            private val themes: Map<Int, String>,
                            private val onBorrarClick: (LegoResponse) -> Unit,
                            private val onLoTengoClick: (LegoResponse) -> Unit,
                            private val onItemClick: (LegoResponse) -> Unit
): RecyclerView.Adapter<LegosGuardadosAdapter.ViewHolder>() {

    private lateinit var context: Context
    private var isWishlistMode: Boolean = false

    fun setWishlistMode(enabled: Boolean) {
        isWishlistMode = enabled
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_lego_guardado, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val lego = legos[position]
        val themeName = themes[lego.theme_id] ?: "Categoría desconocida"
        val legoId = lego.set_num.split("-")[0]
        val legoPiezas = lego.num_parts.toString() + " pzs."

        with(holder) {
            binding.tvIdLego.text = legoId
            binding.tvNombreLego.text = lego.name
            binding.tvCategoriaLego.text = themeName
            binding.tvPiezasLego.text = legoPiezas
            binding.ivLego.load(lego.set_img_url)

            if (isWishlistMode) {
                binding.btnLoTengoLego.visibility = View.VISIBLE
                binding.btnLoTengoLego.setOnClickListener {
                    onLoTengoClick(lego)
                }
            } else {
                binding.btnLoTengoLego.visibility = View.GONE
            }

            binding.btnBorrarLego.setOnClickListener {
                onBorrarClick(lego)
            }

            itemView.setOnClickListener {
                onItemClick(lego)
            }
        }
    }

    override fun getItemCount(): Int = legos.size

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding = ItemLegoGuardadoBinding.bind(view)
    }

    fun setItems(newLegos: List<LegoResponse>) {
        legos.clear()
        legos.addAll(newLegos)
        notifyDataSetChanged()
    }

    fun removeItem(lego: LegoResponse) {
        val position = legos.indexOf(lego)
        if (position != -1) {
            legos.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun addItem(lego: LegoResponse) {
        legos.add(lego)
        notifyItemInserted(legos.size - 1)
    }

}
