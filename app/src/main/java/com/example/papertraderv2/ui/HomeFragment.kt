package com.example.papertraderv2.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.papertraderv2.R
import com.example.papertraderv2.adapters.StockAdapter
import com.example.papertraderv2.models.Stock

// Imports for chart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StockAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // -----------------------
        // RECYCLER VIEW SETUP
        // -----------------------
        recyclerView = view.findViewById(R.id.stocksRecyclerView)

        val yourStocks = listOf(
            Stock("Apple", "AAPL", 188.20),
            Stock("Tesla", "TSLA", 240.55),
            Stock("EUR/USD", "EURUSD", 1.0850)
        )

        val watchlist = listOf(
            Stock("Bitcoin", "BTCUSD", 73000.20),
            Stock("S&P 500", "SPX", 5200.02)
        )

        fun updateList(list: List<Stock>) {
            adapter = StockAdapter(list) { }
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
        }

        updateList(yourStocks)

        // -----------------------
        // TABS SETUP
        // -----------------------
        val tabYour = view.findViewById<Button>(R.id.tabYourStocks)
        val tabWatch = view.findViewById<Button>(R.id.tabWatchlist)

        tabYour.setOnClickListener {
            tabYour.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#00C896"))
            tabWatch.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#1B1B1B"))
            updateList(yourStocks)
        }

        tabWatch.setOnClickListener {
            tabWatch.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#00C896"))
            tabYour.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#1B1B1B"))
            updateList(watchlist)
        }

        // -----------------------
        // PORTFOLIO CHART SETUP
        // -----------------------
        val chart = view.findViewById<LineChart>(R.id.portfolioChart)
        setupPortfolioChart(chart)

        return view
    }

    // -----------------------
    // CHART FUNCTION
    // -----------------------
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
        dataSet.fillAlpha = 150
        dataSet.fillColor = Color.GREEN

        chart.data = LineData(dataSet)

        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.setTouchEnabled(false)
        chart.setPinchZoom(false)
        chart.axisRight.isEnabled = false
        chart.xAxis.isEnabled = false
        chart.axisLeft.isEnabled = false

        chart.invalidate()
    }
}