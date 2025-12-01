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
import com.example.papertraderv2.models.Trade
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.abs

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StockAdapter

    private lateinit var portfolioBalanceText: TextView
    private lateinit var portfolioGrowthText: TextView
    private lateinit var chart: LineChart

    // Your real portfolio — built from trades
    private val yourStocks = mutableListOf<Stock>()

    // Simple in-memory watchlist
    private val watchlist = mutableListOf(
        Stock("Bitcoin", "BTCUSD", 0.0),
        Stock("S&P 500", "SPX", 0.0)
    )

    private var showingYourStocks = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Background unify
        view.setBackgroundColor(Color.parseColor("#0D1B2A"))

        portfolioBalanceText = view.findViewById(R.id.portfolioBalance)
        portfolioGrowthText = view.findViewById(R.id.portfolioGrowth)
        chart = view.findViewById(R.id.portfolioChart)

        recyclerView = view.findViewById(R.id.stocksRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // default adapter (Your Stocks)
        adapter = StockAdapter(yourStocks,
            onClick = { /* later: open details */ },
            onRemove = { stock -> closePosition(stock) }
        )
        recyclerView.adapter = adapter

        // Load data: trades → portfolio → prices
        loadUserPortfolio {
            fetchLivePrices(yourStocks) {
                updatePortfolioUI()
            }
        }

        // Tabs
        val tabYour = view.findViewById<Button>(R.id.tabYourStocks)
        val tabWatch = view.findViewById<Button>(R.id.tabWatchlist)

        // default tab
        tabYour.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#00C896"))
        tabWatch.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1B1B1B"))

        tabYour.setOnClickListener {
            showingYourStocks = true
            tabYour.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#00C896"))
            tabWatch.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1B1B1B"))

            adapter = StockAdapter(yourStocks,
                onClick = { /* open details later */ },
                onRemove = { stock -> closePosition(stock) }
            )
            recyclerView.adapter = adapter
            updatePortfolioUI()
        }

        tabWatch.setOnClickListener {
            showingYourStocks = false
            tabWatch.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#00C896"))
            tabYour.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1B1B1B"))

            adapter = StockAdapter(watchlist,
                onClick = { /* open details later */ },
                onRemove = { stock -> removeFromWatchlist(stock) }
            )
            recyclerView.adapter = adapter
            fetchLivePrices(watchlist, onComplete = { })
        }

        // Setup chart initially
        setupEmptyChart()

        return view
    }

    // -------------------------------------------------------------------
    // LOAD USER PORTFOLIO FROM ROOM TRADES
    // -------------------------------------------------------------------
    private fun loadUserPortfolio(onLoaded: () -> Unit = {}) {
        CoroutineScope(Dispatchers.IO).launch {

            val trades = AppDatabase.getDatabase(requireContext())
                .tradeDao()
                .getAllTrades()

            val grouped = trades.groupBy { it.symbol }

            val portfolio = grouped.mapNotNull { (symbol, list) ->
                val netQty = list.sumOf {
                    if (it.action.equals("Buy", ignoreCase = true)) it.quantity
                    else -it.quantity
                }

                if (netQty == 0.0) {
                    null
                } else {
                    val lastTrade = list.maxByOrNull { it.timestamp } ?: list.first()
                    // temp price = last traded price until live prices load
                    Stock(
                        name = symbol,
                        symbol = symbol,
                        price = lastTrade.price,
                        quantity = netQty
                    )
                }
            }

            requireActivity().runOnUiThread {
                yourStocks.clear()
                yourStocks.addAll(portfolio)
                if (showingYourStocks) adapter.notifyDataSetChanged()
                updatePortfolioUI()
                onLoaded()
            }
        }
    }

    // -------------------------------------------------------------------
    // FETCH LIVE PRICES
    // -------------------------------------------------------------------
    private fun fetchLivePrices(
        stocks: MutableList<Stock>,
        onComplete: () -> Unit = {}
    ) {
        if (stocks.isEmpty()) {
            onComplete()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {

            val updatedStocks = mutableListOf<Stock>()

            for (s in stocks) {
                try {
                    val response = RetrofitClient.api.getTimeSeries(
                        symbol = s.symbol,
                        interval = "1day",
                        outputSize = 1,
                        apiKey = BuildConfig.TWELVE_API_KEY
                    )

                    val latest = response.values?.firstOrNull()
                    val price = latest?.close?.toDoubleOrNull() ?: s.price

                    updatedStocks.add(
                        s.copy(price = price)
                    )

                } catch (e: Exception) {
                    updatedStocks.add(s)
                }
            }

            // Now update list ONCE
            requireActivity().runOnUiThread {
                stocks.clear()
                stocks.addAll(updatedStocks)

                adapter.notifyDataSetChanged()

                if (showingYourStocks && stocks === yourStocks) {
                    updatePortfolioUI()
                }

                onComplete()
            }
        }
    }

    // -------------------------------------------------------------------
    // REMOVE FROM WATCHLIST (UI ONLY)
    // -------------------------------------------------------------------
    private fun removeFromWatchlist(stock: Stock) {
        val index = watchlist.indexOfFirst { it.symbol == stock.symbol }
        if (index != -1) {
            watchlist.removeAt(index)
            adapter.notifyItemRemoved(index)
        }
    }

    // -------------------------------------------------------------------
    // CLOSE POSITION (CREATE OPPOSITE TRADE & REFRESH)
    // -------------------------------------------------------------------
    private fun closePosition(stock: Stock) {
        val qty = stock.quantity
        if (qty == 0.0) return

        val action = if (qty > 0) "Sell" else "Buy"
        val absQty = abs(qty)
        val price = if (stock.price > 0) stock.price else 0.0
        val total = price * absQty

        val trade = Trade(
            symbol = stock.symbol,
            action = action,
            quantity = absQty,
            price = price,
            total = total
        )

        CoroutineScope(Dispatchers.IO).launch {
            AppDatabase.getDatabase(requireContext())
                .tradeDao()
                .insertTrade(trade)

            // Reload portfolio from trades so it's consistent
            loadUserPortfolio {
                fetchLivePrices(yourStocks) {
                    updatePortfolioUI()
                }
            }
        }
    }

    // -------------------------------------------------------------------
    // PORTFOLIO UI + CHART
    // -------------------------------------------------------------------
    private fun updatePortfolioUI() {
        if (yourStocks.isEmpty()) {
            portfolioBalanceText.text = "$0.00"
            portfolioGrowthText.text = "+0.00% Today"
            setupEmptyChart()
            return
        }

        // Use current prices * quantity (only long/positive for now)
        val totalValue = yourStocks.sumOf { it.price * it.quantity }

        portfolioBalanceText.text = "$${"%.2f".format(totalValue.coerceAtLeast(0.0))}"

        // Fake a simple "day curve" based on current value for the visual
        val startValue = totalValue * 0.96
        val mid1 = totalValue * 0.985
        val mid2 = totalValue * 0.99
        val endValue = totalValue

        val entries = ArrayList<Entry>().apply {
            add(Entry(0f, startValue.toFloat()))
            add(Entry(1f, mid1.toFloat()))
            add(Entry(2f, mid2.toFloat()))
            add(Entry(3f, endValue.toFloat()))
        }

        val growthPercent = if (startValue > 0) {
            ((endValue - startValue) / startValue) * 100.0
        } else 0.0

        val sign = if (growthPercent >= 0) "+" else "-"
        portfolioGrowthText.text =
            "$sign${"%.2f".format(abs(growthPercent))}% Today"

        val dataSet = LineDataSet(entries, "Portfolio Growth").apply {
            color = Color.parseColor("#00C896")
            lineWidth = 2.5f
            setDrawCircles(false)
            setDrawValues(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(true)
            fillColor = Color.parseColor("#00C896")
            fillAlpha = 80
        }

        chart.data = LineData(dataSet)
        styleChart()
        chart.invalidate()
    }

    private fun setupEmptyChart() {
        val entries = ArrayList<Entry>().apply {
            add(Entry(0f, 0f))
            add(Entry(1f, 0f))
            add(Entry(2f, 0f))
            add(Entry(3f, 0f))
        }

        val dataSet = LineDataSet(entries, "Portfolio").apply {
            color = Color.parseColor("#00C896")
            lineWidth = 2.5f
            setDrawCircles(false)
            setDrawValues(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(true)
            fillColor = Color.parseColor("#00C896")
            fillAlpha = 40
        }

        chart.data = LineData(dataSet)
        styleChart()
        chart.invalidate()
    }

    private fun styleChart() {
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.axisRight.isEnabled = false

        chart.axisLeft.isEnabled = false
        chart.xAxis.isEnabled = false

        chart.setTouchEnabled(false)
        chart.setScaleEnabled(false)
        chart.setPinchZoom(false)
    }
}