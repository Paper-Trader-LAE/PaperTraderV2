package com.example.papertraderv2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.papertraderv2.databinding.ItemHomeFilterBinding

class HomeFilterAdapter(
    private val items: List<String>,
    private var selectedIndex: Int = 0,
    private val onSelected: (Int) -> Unit
) : RecyclerView.Adapter<HomeFilterAdapter.VH>() {

    inner class VH(val binding: ItemHomeFilterBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemHomeFilterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.binding.filterChip.text = items[position]
        holder.binding.filterChip.isSelected = (position == selectedIndex)

        // Change text color depending on selection (green chip uses dark text)
        holder.binding.filterChip.setTextColor(
            if (position == selectedIndex) android.graphics.Color.parseColor("#0D1B2A")
            else android.graphics.Color.WHITE
        )

        holder.binding.filterChip.setOnClickListener {
            selectedIndex = position
            notifyDataSetChanged()
            onSelected(position)
        }
    }

    override fun getItemCount(): Int = items.size
}