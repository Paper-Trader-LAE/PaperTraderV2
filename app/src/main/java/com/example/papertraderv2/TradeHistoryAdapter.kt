package com.example.papertraderv2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.papertraderv2.models.Trade
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TradeHistoryAdapter(
    private val list: List<Trade>
) : RecyclerView.Adapter<TradeHistoryAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val badge: TextView = view.findViewById(R.id.tradeBadge)
        val title: TextView = view.findViewById(R.id.tradeTitle)
        val meta: TextView = view.findViewById(R.id.tradeMeta)
        val price: TextView = view.findViewById(R.id.tradePrice)
        val time: TextView = view.findViewById(R.id.tradeTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_trade, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val trade = list[position]
        val action = trade.action.trim().uppercase(Locale.getDefault())
        val isBuy = action == "BUY"

        holder.itemView.setBackgroundResource(
            if (isBuy) R.drawable.bg_trade_buy_card else R.drawable.bg_trade_sell_card
        )

        holder.badge.text = if (isBuy) "BUY" else "SELL"
        holder.badge.setBackgroundResource(
            if (isBuy) R.drawable.bg_trade_badge_buy else R.drawable.bg_trade_badge_sell
        )

        val formattedAction = if (isBuy) "Buy" else "Sell"
        holder.title.text = "$formattedAction ${trade.symbol.uppercase(Locale.getDefault())}"

        holder.meta.text = "${"%.2f".format(trade.quantity)} lots"
        holder.price.text = "$${"%.2f".format(trade.price)}"

        val df = SimpleDateFormat("MMM d", Locale.getDefault())
        holder.time.text = df.format(Date(trade.timestamp))
    }

    override fun getItemCount(): Int = list.size
}