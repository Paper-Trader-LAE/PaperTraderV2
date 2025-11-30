package com.example.papertraderv2.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.papertraderv2.BuildConfig
import com.example.papertraderv2.R
import com.example.papertraderv2.RetrofitClient
import com.example.papertraderv2.adapters.StockAdapter
import com.example.papertraderv2.data.AppDatabase
import com.example.papertraderv2.models.Stock

// Chart imports
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StockAdapter

    // Your real portfolio — will be replaced by user trades
    private val yourStocks = mutableListOf<Stock>()

    // Static watchlist for now
    private val watchlist = mutableListOf(
        Stock("Bitcoin", "BTCUSD", 0.0),
        Stock("S&P 500", "SPX", 0.0)
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.stocksRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = StockAdapter(yourStocks) { }
        recyclerView.adapter = adapter

        // 🔥 Load user portfolio first
        loadUserPortfolio {
            // After portfolio loads → fetch live prices
            fetchLivePrices(yourStocks)
        }

        // Tabs
        val tabYour = view.findViewById<Button>(R.id.tabYourStocks)
        val tabWatch = view.findViewById<Button>(R.id.tabWatchlist)

        tabYour.setOnClickListener {
            tabYour.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#00C896"))
            tabWatch.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1B1B1B"))

            adapter = StockAdapter(yourStocks) {}
            recyclerView.adapter = adapter

            fetchLivePrices(yourStocks)
        }

        tabWatch.setOnClickListener {
            tabWatch.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#00C896"))
            tabYour.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1B1B1B"))

            adapter = StockAdapter(watchlist) {}
            recyclerView.adapter = adapter

            fetchLivePrices(watchlist)
        }

        // Chart
        val chart = view.findViewById<LineChart>(R.id.portfolioChart)
        setupPortfolioChart(chart)

        return view
    }

    // -------------------------------------------------------------------
    // LOAD USER PORTFOLIO FROM ROOM
    // -------------------------------------------------------------------
    private fun loadUserPortfolio(onLoaded: () -> Unit = {}) {
        CoroutineScope(Dispatchers.IO).launch {

            val trades = AppDatabase.getDatabase(requireContext())
                .tradeDao()
                .getAllTrades()

            // Group by symbol & calculate net quantity
            val grouped = trades.groupBy { it.symbol }

            val portfolio = grouped.mapNotNull { (symbol, list) ->
                val qty = list.sumOf {
                    if (it.action == "Buy") it.quantity else -it.quantity
                }

                if (qty != 0.0)
                    Stock(symbol, symbol, qty)
                else
                    null
            }

            requireActivity().runOnUiThread {
                yourStocks.clear()
                yourStocks.addAll(portfolio)
                adapter.notifyDataSetChanged()

                onLoaded() // Callback when finished
            }
        }
    }

    // -------------------------------------------------------------------
    // FETCH LIVE PRICES
    // -------------------------------------------------------------------
    private fun fetchLivePrices(stocks: MutableList<Stock>) {
        CoroutineScope(Dispatchers.IO).launch {
            for (i in stocks.indices) {
                try {
                    val response = RetrofitClient.api.getTimeSeries(
                        symbol = stocks[i].symbol,
                        interval = "1day",
                        outputSize = 1,
                        apiKey = BuildConfig.TWELVE_API_KEY
                    )

                    val latest = response.values?.firstOrNull()
                    val price = latest?.close?.toDoubleOrNull() ?: continue

                    stocks[i] = stocks[i].copy(price = price)

                    requireActivity().runOnUiThread {
                        adapter.notifyItemChanged(i)
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    // -------------------------------------------------------------------
    // CHART
    // -------------------------------------------------------------------
    private fun setupPortfolioChart(chart: LineChart) {
        val entries = ArrayList<Entry>().apply {
            add(Entry(0f, 100f))
            add(Entry(1f, 103f))
            add(Entry(2f, 120f))
            add(Entry(3f, 118f))
            add(Entry(4f, 145f))
        }

        val dataSet = LineDataSet(entries, "Portfolio Growth")
        dataSet.color = Color.GREEN
        dataSet.lineWidth = 2.5f
        dataSet.setDrawCircles(false)
        dataSet.setDrawValues(false)
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataSet.setDrawFilled(true)
        dataSet.fillColor = Color.GREEN
        dataSet.fillAlpha = 150

        chart.data = LineData(dataSet)
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.axisRight.isEnabled = false
        chart.axisLeft.isEnabled = false
        chart.xAxis.isEnabled = false

        chart.invalidate()
    }
}