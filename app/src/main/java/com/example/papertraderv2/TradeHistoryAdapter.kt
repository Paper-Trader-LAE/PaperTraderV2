package com.example.papertraderv2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.papertraderv2.R
import com.example.papertraderv2.models.Trade
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TradeHistoryAdapter(
    private val list: List<Trade>
) : RecyclerView.Adapter<TradeHistoryAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tradeTitle)
        val meta: TextView = view.findViewById(R.id.tradeMeta)
        val time: TextView = view.findViewById(R.id.tradeTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_trade, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val t = list[position]
        holder.title.text = "${t.action.uppercase()} ${t.symbol}"

        holder.meta.text = "Qty: ${t.quantity}  •  Price: $${"%.4f".format(t.price)}  •  Total: $${"%.2f".format(t.total)}"

        val df = SimpleDateFormat("MMM d", Locale.getDefault())
        holder.time.text = df.format(Date(t.timestamp))
    }

    override fun getItemCount(): Int = list.size
}