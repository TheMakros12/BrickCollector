package com.example.brickcollector.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.brickcollector.R
import com.example.brickcollector.data.LegoResponse
import com.example.brickcollector.databinding.ItemLegoBuscarBinding
import coil.load

class LegoAdapter(private val legos: MutableList<LegoResponse>,
                  private val themes: Map<Int, String>,
                  private val onGuardarClick: (LegoResponse) -> Unit,
                  private val onWishlistClick: (LegoResponse) -> Unit,
                  private val onItemClick: (LegoResponse) -> Unit
): RecyclerView.Adapter<LegoAdapter.ViewHolder>() {

    private lateinit var context: Context
    private val savedSetNums = mutableSetOf<String>()
    private val wishlistSetNums = mutableSetOf<String>()

    fun updateSavedSetNums(newSavedSetNums: Collection<String>) {
        savedSetNums.clear()
        savedSetNums.addAll(newSavedSetNums)
        notifyDataSetChanged()
    }

    fun updateWishlistSetNums(newWishlistSetNums: Collection<String>) {
        wishlistSetNums.clear()
        wishlistSetNums.addAll(newWishlistSetNums)
        notifyDataSetChanged()
    }

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
            binding.ivLego.load(lego.set_img_url)

            val isOwned = savedSetNums.contains(lego.set_num)
            val isWishlisted = wishlistSetNums.contains(lego.set_num)

            if (isOwned) {
                binding.btnGuardarLego.text = "Guardado"
                binding.btnGuardarLego.isEnabled = false
                binding.btnGuardarLego.strokeColor = android.content.res.ColorStateList.valueOf(context.getColor(R.color.text_gray))
                binding.btnGuardarLego.strokeWidth = 2
                binding.btnGuardarLego.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.TRANSPARENT)
                binding.btnGuardarLego.setTextColor(context.getColor(R.color.text_gray))

                binding.btnWishlistLego.visibility = View.GONE
            } else {
                binding.btnGuardarLego.text = "Guardar"
                binding.btnGuardarLego.isEnabled = true
                binding.btnGuardarLego.strokeWidth = 0
                binding.btnGuardarLego.backgroundTintList = android.content.res.ColorStateList.valueOf(context.getColor(R.color.lego_red))
                binding.btnGuardarLego.setTextColor(context.getColor(R.color.white))

                binding.btnWishlistLego.visibility = View.VISIBLE
                if (isWishlisted) {
                    binding.btnWishlistLego.text = "Deseado"
                    binding.btnWishlistLego.isEnabled = false
                    binding.btnWishlistLego.strokeColor = android.content.res.ColorStateList.valueOf(context.getColor(R.color.text_gray))
                    binding.btnWishlistLego.strokeWidth = 2
                    binding.btnWishlistLego.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.TRANSPARENT)
                    binding.btnWishlistLego.setTextColor(context.getColor(R.color.text_gray))
                } else {
                    binding.btnWishlistLego.text = "Deseo"
                    binding.btnWishlistLego.isEnabled = true
                    binding.btnWishlistLego.strokeColor = android.content.res.ColorStateList.valueOf(context.getColor(R.color.lego_red))
                    binding.btnWishlistLego.strokeWidth = 1
                    binding.btnWishlistLego.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.TRANSPARENT)
                    binding.btnWishlistLego.setTextColor(context.getColor(R.color.lego_red))
                }
            }

            binding.btnGuardarLego.setOnClickListener {
                onGuardarClick(lego)
            }

            binding.btnWishlistLego.setOnClickListener {
                onWishlistClick(lego)
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
        val diffCallback = LegoDiffCallback(legos.toList(), nuevosLegos)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        legos.clear()
        legos.addAll(nuevosLegos)
        diffResult.dispatchUpdatesTo(this)
    }

    private class LegoDiffCallback(
        private val oldList: List<LegoResponse>,
        private val newList: List<LegoResponse>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size
        override fun areItemsTheSame(oldPos: Int, newPos: Int) =
            oldList[oldPos].set_num == newList[newPos].set_num
        override fun areContentsTheSame(oldPos: Int, newPos: Int) =
            oldList[oldPos] == newList[newPos]
    }

}
