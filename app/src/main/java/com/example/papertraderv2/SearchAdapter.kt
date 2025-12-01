package com.example.papertraderv2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.papertraderv2.R
import com.example.papertraderv2.models.Stock

class SearchAdapter(
    private val list: List<Stock>,
    private val onClick: (Stock) -> Unit
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.searchStockName)
        val symbol: TextView = itemView.findViewById(R.id.searchStockSymbol)
        val price: TextView = itemView.findViewById(R.id.searchStockPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_stock, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val item = list[position]

        holder.name.text = item.name
        holder.symbol.text = item.symbol
        holder.price.text = if (item.price != 0.0) "$${"%.4f".format(item.price)}" else "--"

        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = list.size
}