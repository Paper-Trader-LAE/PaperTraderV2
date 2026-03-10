package com.example.papertraderv2.ui

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.papertraderv2.BuildConfig
import com.example.papertraderv2.RetrofitClient
import com.example.papertraderv2.data.AppDatabase
import com.example.papertraderv2.databinding.FragmentTradeBinding
import com.example.papertraderv2.models.Trade
import com.example.papertraderv2.utils.SymbolMapper
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.max

class TradeFragment : Fragment() {

    private var _binding: FragmentTradeBinding? = null
    private val binding get() = _binding!!

    private var selectedCategory = "Indices"
    private var selectedSymbol = "SPY"
    private var livePrice = 0.0

    private val handler = Handler(Looper.getMainLooper())
    private val refreshRunnable = object : Runnable {
        override fun run() {
            if (selectedSymbol.isNotBlank()) {
                loadPrice(selectedSymbol)   // Finnhub
            }
            handler.postDelayed(this, 15000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTradeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupCategorySpinner()
        setupSearch()
        setupLotControls()

        val incomingSymbol = arguments?.getString("symbol")
        if (!incomingSymbol.isNullOrBlank()) {
            selectedSymbol = incomingSymbol.uppercase()
        }

        binding.currentSymbolText.text = selectedSymbol
        binding.searchInput.setText(selectedSymbol)

        loadPrice(selectedSymbol)
        loadCandleData(selectedSymbol)

        binding.btnBuy.setOnClickListener { submitTrade("Buy") }
        binding.btnSell.setOnClickListener { submitTrade("Sell") }
    }

    override fun onResume() {
        super.onResume()
        handler.post(refreshRunnable)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(refreshRunnable)
    }

    private fun setupCategorySpinner() {
        val items = listOf("Indices", "Stocks", "Crypto")

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            items
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerCategory.adapter = adapter
        binding.spinnerCategory.setSelection(0)

        binding.spinnerCategory.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedCategory = items[position]

                    selectedSymbol = when (selectedCategory) {
                        "Indices" -> "SPY"
                        "Stocks" -> "AAPL"
                        else -> "SPY"
                    }

                    binding.currentSymbolText.text = selectedSymbol
                    binding.searchInput.setText(selectedSymbol)

                    loadPrice(selectedSymbol)
                    loadCandleData(selectedSymbol)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    private fun setupSearch() {
        binding.searchInput.setOnEditorActionListener { v, actionId, event ->
            val isSearchAction = actionId == EditorInfo.IME_ACTION_SEARCH
            val isEnterKey =
                event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN

            if (isSearchAction || isEnterKey) {
                val rawQuery = v.text.toString().trim()
                if (rawQuery.isNotBlank()) {
                    val symbol = normalizeQuery(rawQuery)
                    selectedSymbol = symbol
                    binding.currentSymbolText.text = symbol
                    loadPrice(symbol)
                    loadCandleData(symbol)
                }
                true
            } else {
                false
            }
        }
    }

    private fun setupLotControls() {
        binding.btnMinus.setOnClickListener {
            val current = binding.inputLotSize.text.toString().toDoubleOrNull() ?: 0.01
            val updated = max(0.01, current - 0.01)
            binding.inputLotSize.setText(String.format("%.2f", updated))
        }

        binding.btnPlus.setOnClickListener {
            val current = binding.inputLotSize.text.toString().toDoubleOrNull() ?: 0.01
            val updated = current + 0.01
            binding.inputLotSize.setText(String.format("%.2f", updated))
        }
    }

    private fun normalizeQuery(query: String): String {
        return SymbolMapper.toSymbol(query).uppercase()
    }

    private fun loadPrice(symbol: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.api.getQuote(
                    symbol = symbol,
                    token = BuildConfig.FINNHUB_API_KEY
                )

                livePrice = response.c ?: 0.0

                requireActivity().runOnUiThread {
                    binding.textLivePrice.text =
                        if (livePrice > 0.0) "$${"%.4f".format(livePrice)}"
                        else "Price unavailable"
                }
            } catch (e: Exception) {
                requireActivity().runOnUiThread {
                    binding.textLivePrice.text = "Price unavailable"
                }
            }
        }
    }

    private fun loadCandleData(symbol: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.alphaApi.getDailySeries(
                    symbol = symbol,
                    apiKey = BuildConfig.ALPHA_VANTAGE_API_KEY
                )

                val series = response.timeSeriesDaily

                if (!response.note.isNullOrBlank()) {
                    requireActivity().runOnUiThread {
                        binding.tradeCandleChart.clear()
                        binding.tradeCandleChart.setNoDataText("API limit reached, try again later")
                        binding.tradeCandleChart.invalidate()
                    }
                    return@launch
                }

                if (!response.errorMessage.isNullOrBlank() || series.isNullOrEmpty()) {
                    requireActivity().runOnUiThread {
                        binding.tradeCandleChart.clear()
                        binding.tradeCandleChart.setNoDataText("No chart data")
                        binding.tradeCandleChart.invalidate()
                    }
                    return@launch
                }

                val entries = ArrayList<CandleEntry>()
                var index = 0f

                val sortedSeries = series.toSortedMap()

                for ((_, candle) in sortedSeries) {
                    val open = candle.open?.toFloatOrNull()
                    val high = candle.high?.toFloatOrNull()
                    val low = candle.low?.toFloatOrNull()
                    val close = candle.close?.toFloatOrNull()

                    if (open != null && high != null && low != null && close != null) {
                        entries.add(
                            CandleEntry(
                                index,
                                high,
                                low,
                                open,
                                close
                            )
                        )
                        index += 1f
                    }
                }

                if (entries.isEmpty()) {
                    requireActivity().runOnUiThread {
                        binding.tradeCandleChart.clear()
                        binding.tradeCandleChart.setNoDataText("No chart data")
                        binding.tradeCandleChart.invalidate()
                    }
                    return@launch
                }

                val set = CandleDataSet(entries, "Daily Price").apply {
                    decreasingColor = Color.RED
                    decreasingPaintStyle = Paint.Style.FILL
                    increasingColor = Color.GREEN
                    increasingPaintStyle = Paint.Style.FILL
                    neutralColor = Color.WHITE
                    shadowColorSameAsCandle = true
                    setDrawValues(false)
                }

                val data = CandleData(set)

                requireActivity().runOnUiThread {
                    binding.tradeCandleChart.data = data
                    binding.tradeCandleChart.description.isEnabled = false
                    binding.tradeCandleChart.legend.isEnabled = false
                    binding.tradeCandleChart.axisRight.isEnabled = false
                    binding.tradeCandleChart.axisLeft.textColor = Color.WHITE
                    binding.tradeCandleChart.xAxis.textColor = Color.WHITE
                    binding.tradeCandleChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
                    binding.tradeCandleChart.setBackgroundColor(Color.TRANSPARENT)
                    binding.tradeCandleChart.invalidate()
                }

            } catch (e: Exception) {
                requireActivity().runOnUiThread {
                    binding.tradeCandleChart.clear()
                    binding.tradeCandleChart.setNoDataText("Failed to load chart")
                    binding.tradeCandleChart.invalidate()
                }
            }
        }
    }

    private fun submitTrade(action: String) {
        val qty = binding.inputLotSize.text.toString().toDoubleOrNull()

        if (selectedSymbol.isBlank()) {
            Toast.makeText(requireContext(), "Choose a market first", Toast.LENGTH_SHORT).show()
            return
        }

        if (qty == null || qty <= 0.0) {
            Toast.makeText(requireContext(), "Enter a valid lot size", Toast.LENGTH_SHORT).show()
            return
        }

        if (livePrice <= 0.0) {
            Toast.makeText(requireContext(), "Live price unavailable", Toast.LENGTH_SHORT).show()
            return
        }

        val trade = Trade(
            id = 0,
            symbol = selectedSymbol,
            action = action,
            quantity = qty,
            price = livePrice,
            total = qty * livePrice
        )

        CoroutineScope(Dispatchers.IO).launch {
            AppDatabase.getDatabase(requireContext()).tradeDao().insertTrade(trade)

            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), "$action order placed!", Toast.LENGTH_SHORT).show()
                parentFragmentManager.setFragmentResult("trade_made", Bundle())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(refreshRunnable)
        _binding = null
    }
}