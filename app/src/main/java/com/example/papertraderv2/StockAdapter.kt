package com.example.papertraderv2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.papertraderv2.R
import com.example.papertraderv2.models.Stock

class StockAdapter(
    private val list: MutableList<Stock>,
    private val onClick: (Stock) -> Unit,
    private val onRemove: ((Stock) -> Unit)? = null
) : RecyclerView.Adapter<StockAdapter.StockViewHolder>() {

    inner class StockViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.stockName)
        val symbol: TextView = view.findViewById(R.id.stockSymbol)
        val price: TextView = view.findViewById(R.id.stockPrice)
        val quantity: TextView = view.findViewById(R.id.stockQuantity)
        val remove: TextView = view.findViewById(R.id.stockRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_stock, parent, false)
        return StockViewHolder(view)
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        val item = list[position]

        holder.name.text = item.name
        holder.symbol.text = item.symbol
        holder.price.text = "$${"%.4f".format(item.price)}"

        // show quantity if > 0 (for "Your Stocks")
        if (item.quantity != 0.0) {
            holder.quantity.visibility = View.VISIBLE
            holder.quantity.text = "${item.quantity} units"
        } else {
            holder.quantity.visibility = View.GONE
        }

        holder.itemView.setOnClickListener { onClick(item) }

        holder.remove.setOnClickListener {
            onRemove?.invoke(item)
        }
    }

    override fun getItemCount(): Int = list.size
}