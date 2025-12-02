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

    private val yourStocks = mutableListOf<Stock>()

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

        view.setBackgroundColor(Color.parseColor("#0D1B2A"))

        portfolioBalanceText = view.findViewById(R.id.portfolioBalance)
        portfolioGrowthText = view.findViewById(R.id.portfolioGrowth)
        chart = view.findViewById(R.id.portfolioChart)

        recyclerView = view.findViewById(R.id.stocksRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = StockAdapter(
            yourStocks,
            onClick = {},
            onRemove = { stock -> closePosition(stock) }
        )
        recyclerView.adapter = adapter

        // FIRST LOAD
        loadUserPortfolio {
            fetchLivePrices(yourStocks) {
                updatePortfolioUI()
            }
        }

        setupTabs(view)
        setupEmptyChart()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // 🔥 FIXED: This was below return (never executed)
        parentFragmentManager.setFragmentResultListener("trade_made", this) { _, _ ->
            loadUserPortfolio {
                fetchLivePrices(yourStocks) {
                    adapter.notifyDataSetChanged()
                    updatePortfolioUI()
                }
            }
        }
    }

    // -------------------------------------------------------------------
    // TABS
    // -------------------------------------------------------------------
    private fun setupTabs(view: View) {
        val tabYour = view.findViewById<Button>(R.id.tabYourStocks)
        val tabWatch = view.findViewById<Button>(R.id.tabWatchlist)

        tabYour.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#00C896"))
        tabWatch.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1B1B1B"))

        tabYour.setOnClickListener {
            showingYourStocks = true
            tabYour.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#00C896"))
            tabWatch.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1B1B1B"))

            adapter = StockAdapter(
                yourStocks,
                onClick = {},
                onRemove = { stock -> closePosition(stock) }
            )
            recyclerView.adapter = adapter
            updatePortfolioUI()
        }

        tabWatch.setOnClickListener {
            showingYourStocks = false
            tabWatch.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#00C896"))
            tabYour.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1B1B1B"))

            adapter = StockAdapter(
                watchlist,
                onClick = {},
                onRemove = { stock -> removeFromWatchlist(stock) }
            )
            recyclerView.adapter = adapter

            fetchLivePrices(watchlist)
        }
    }

    // -------------------------------------------------------------------
    // LOAD PORTFOLIO FROM ROOM
    // -------------------------------------------------------------------
    private fun loadUserPortfolio(onLoaded: () -> Unit = {}) {
        CoroutineScope(Dispatchers.IO).launch {
            val trades = AppDatabase.getDatabase(requireContext())
                .tradeDao()
                .getAllTrades()

            val grouped = trades.groupBy { it.symbol }

            val portfolio = grouped.mapNotNull { (symbol, list) ->
                val qty = list.sumOf {
                    if (it.action.equals("BUY", true)) it.quantity else -it.quantity
                }

                if (qty == 0.0) return@mapNotNull null

                val lastTrade = list.maxByOrNull { it.timestamp }!!

                Stock(
                    name = symbol,
                    symbol = symbol,
                    price = lastTrade.price,
                    quantity = qty
                )
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
    // LIVE PRICES
    // -------------------------------------------------------------------
    private fun fetchLivePrices(
        stocks: MutableList<Stock>,
        onComplete: () -> Unit = {}
    ) {

        CoroutineScope(Dispatchers.IO).launch {

            val fresh = stocks.map { s ->
                try {
                    val res = RetrofitClient.api.getTimeSeries(
                        symbol = s.symbol,
                        interval = "1day",
                        outputSize = 1,
                        apiKey = BuildConfig.TWELVE_API_KEY
                    )
                    val price = res.values?.firstOrNull()?.close?.toDoubleOrNull() ?: s.price
                    s.copy(price = price)
                } catch (e: Exception) {
                    s
                }
            }

            requireActivity().runOnUiThread {
                stocks.clear()
                stocks.addAll(fresh)
                adapter.notifyDataSetChanged()

                if (showingYourStocks && stocks === yourStocks) {
                    updatePortfolioUI()
                }

                onComplete()
            }
        }
    }

    // -------------------------------------------------------------------
    // REMOVE WATCHLIST ITEM (FIXED)
    // -------------------------------------------------------------------
    private fun removeFromWatchlist(stock: Stock) {
        val index = watchlist.indexOfFirst { it.symbol == stock.symbol }
        if (index != -1) {
            watchlist.removeAt(index)
            adapter.notifyDataSetChanged()
        }
    }

    // -------------------------------------------------------------------
    // CLOSE POSITION
    // -------------------------------------------------------------------
    private fun closePosition(stock: Stock) {
        val qty = stock.quantity
        if (qty == 0.0) return

        val action = if (qty > 0) "Sell" else "Buy"

        val trade = Trade(
            symbol = stock.symbol,
            action = action,
            quantity = abs(qty),
            price = stock.price,
            total = stock.price * abs(qty)
        )

        CoroutineScope(Dispatchers.IO).launch {
            AppDatabase.getDatabase(requireContext())
                .tradeDao()
                .insertTrade(trade)

            loadUserPortfolio {
                fetchLivePrices(yourStocks) {
                    adapter.notifyDataSetChanged()
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

        val totalValue = yourStocks.sumOf { it.price * it.quantity }
        portfolioBalanceText.text = "$${"%.2f".format(totalValue)}"

        val startValue = totalValue * 0.96
        val entries = listOf(
            Entry(0f, startValue.toFloat()),
            Entry(1f, (startValue * 1.01).toFloat()),
            Entry(2f, (startValue * 1.02).toFloat()),
            Entry(3f, totalValue.toFloat())
        )

        val growth = ((totalValue - startValue) / startValue) * 100
        val sign = if (growth >= 0) "+" else "-"
        portfolioGrowthText.text = "$sign${"%.2f".format(abs(growth))}% Today"

        val ds = LineDataSet(entries, "Growth").apply {
            color = Color.parseColor("#00C896")
            setDrawValues(false)
            setDrawCircles(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(true)
            fillColor = Color.parseColor("#00C896")
            fillAlpha = 70
        }

        chart.data = LineData(ds)
        styleChart()
        chart.invalidate()
    }

    private fun setupEmptyChart() {
        val ds = LineDataSet(
            listOf(Entry(0f, 0f), Entry(1f, 0f), Entry(2f, 0f), Entry(3f, 0f)),
            "Empty"
        ).apply {
            color = Color.parseColor("#00C896")
            setDrawValues(false)
            setDrawCircles(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(true)
            fillColor = Color.parseColor("#00C896")
            fillAlpha = 40
        }

        chart.data = LineData(ds)
        styleChart()
    }

    private fun styleChart() {
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.axisRight.isEnabled = false
        chart.axisLeft.isEnabled = false
        chart.xAxis.isEnabled = false
        chart.setTouchEnabled(false)
        chart.setScaleEnabled(false)
    }
}