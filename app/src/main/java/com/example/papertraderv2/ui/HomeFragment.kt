package com.example.papertraderv2.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.papertraderv2.BuildConfig
import com.example.papertraderv2.HomeFilterAdapter
import com.example.papertraderv2.R
import com.example.papertraderv2.RetrofitClient
import com.example.papertraderv2.TradeHistoryAdapter
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
    private lateinit var portfolioBalanceText: TextView
    private lateinit var portfolioGrowthText: TextView
    private lateinit var chart: LineChart

    private lateinit var filterRecycler: RecyclerView
    private lateinit var filterAdapter: HomeFilterAdapter

    private val yourStocks = mutableListOf<Stock>()
    private val watchlist = mutableListOf<Stock>()

    private val filters = listOf("Your Trades", "Watchlist", "View Past Trades")
    private var selectedFilterIndex = 0
    private var showingPastTrades = false

    private lateinit var stockAdapter: StockAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)


        portfolioBalanceText = view.findViewById(R.id.portfolioBalance)
        portfolioGrowthText = view.findViewById(R.id.portfolioGrowth)
        chart = view.findViewById(R.id.portfolioChart)

        recyclerView = view.findViewById(R.id.stocksRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        filterRecycler = view.findViewById(R.id.homeFilterRecycler)
        filterRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        filterAdapter = HomeFilterAdapter(filters, selectedFilterIndex) { index ->
            selectedFilterIndex = index
            switchList(index)
        }

        filterRecycler.adapter = filterAdapter

        setupEmptyChart()
        switchList(0)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        parentFragmentManager.setFragmentResultListener("trade_made", this) { _, _ ->
            when (selectedFilterIndex) {
                0 -> loadUserPortfolio {
                    fetchLivePrices(yourStocks) { updatePortfolioUI() }
                }
                1 -> fetchLivePrices(watchlist)
                2 -> loadPastTrades()
            }
        }
    }

    private fun switchList(index: Int) {
        when (index) {
            0 -> {
                showingPastTrades = false

                stockAdapter = StockAdapter(
                    yourStocks,
                    onClick = {},
                    onRemove = { stock -> closePosition(stock) }
                )

                recyclerView.adapter = stockAdapter

                loadUserPortfolio {
                    fetchLivePrices(yourStocks) { updatePortfolioUI() }
                }
            }

            1 -> {
                showingPastTrades = false

                stockAdapter = StockAdapter(
                    watchlist,
                    onClick = {},
                    onRemove = { stock -> removeFromWatchlist(stock) }
                )

                recyclerView.adapter = stockAdapter
                fetchLivePrices(watchlist)
            }

            2 -> {
                showingPastTrades = true
                loadPastTrades()
            }
        }
    }

    private fun loadPastTrades() {
        CoroutineScope(Dispatchers.IO).launch {
            val trades = AppDatabase.getDatabase(requireContext())
                .tradeDao()
                .getAllTrades()

            requireActivity().runOnUiThread {
                recyclerView.adapter = TradeHistoryAdapter(trades)
            }
        }
    }

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

                if (!showingPastTrades && selectedFilterIndex == 0 && ::stockAdapter.isInitialized) {
                    stockAdapter.notifyDataSetChanged()
                }

                updatePortfolioUI()
                onLoaded()
            }
        }
    }

    private fun fetchLivePrices(
        stocks: MutableList<Stock>,
        onComplete: () -> Unit = {}
    ) {
        CoroutineScope(Dispatchers.IO).launch {

            val fresh = stocks.map { stock ->
                try {
                    val response = RetrofitClient.api.getQuote(
                        symbol = stock.symbol,
                        token = BuildConfig.FINNHUB_API_KEY
                    )

                    val price = response.c ?: stock.price
                    stock.copy(price = price)
                } catch (e: Exception) {
                    stock
                }
            }

            requireActivity().runOnUiThread {
                stocks.clear()
                stocks.addAll(fresh)

                if (!showingPastTrades && ::stockAdapter.isInitialized) {
                    stockAdapter.notifyDataSetChanged()
                }

                if (selectedFilterIndex == 0) {
                    updatePortfolioUI()
                }

                onComplete()
            }
        }
    }

    private fun removeFromWatchlist(stock: Stock) {
        val index = watchlist.indexOfFirst { it.symbol == stock.symbol }

        if (index != -1) {
            watchlist.removeAt(index)

            if (!showingPastTrades && selectedFilterIndex == 1 && ::stockAdapter.isInitialized) {
                stockAdapter.notifyDataSetChanged()
            }
        }
    }

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

            when (selectedFilterIndex) {
                0 -> loadUserPortfolio {
                    fetchLivePrices(yourStocks) { updatePortfolioUI() }
                }
                2 -> loadPastTrades()
            }
        }
    }

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

        val dataSet = LineDataSet(entries, "Growth")
        chart.data = LineData(dataSet)
        chart.invalidate()
    }

    private fun setupEmptyChart() {
        val dataSet = LineDataSet(
            listOf(
                Entry(0f, 0f),
                Entry(1f, 0f),
                Entry(2f, 0f),
                Entry(3f, 0f)
            ),
            "Empty"
        )

        chart.data = LineData(dataSet)
        chart.invalidate()
    }
}