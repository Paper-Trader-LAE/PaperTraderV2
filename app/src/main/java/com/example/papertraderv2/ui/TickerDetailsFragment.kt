package com.example.papertraderv2.ui

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.papertraderv2.BuildConfig
import com.example.papertraderv2.R
import com.example.papertraderv2.RetrofitClient
import com.example.papertraderv2.databinding.FragmentTickerDetailsBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TickerDetailsFragment : Fragment() {

    private var _binding: FragmentTickerDetailsBinding? = null
    private val binding get() = _binding!!

    private var symbol: String = ""
    private var displayName: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTickerDetailsBinding.inflate(inflater, container, false)

        Log.e("LAYOUT_CHECK", "Loaded Ticker Details Layout")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        displayName = arguments?.getString("displayName") ?: ""
        symbol = arguments?.getString("symbol") ?: ""

        binding.tickerTitle.text =
            if (displayName.isNotBlank()) displayName else symbol

        loadPrice(symbol)
        loadCandleData(symbol)

        binding.btnBuy.setOnClickListener { openTradeScreen(symbol, "Buy") }
        binding.btnSell.setOnClickListener { openTradeScreen(symbol, "Sell") }

        binding.btnWatchlist.setOnClickListener {
            Toast.makeText(requireContext(), "$symbol added to watchlist", Toast.LENGTH_SHORT).show()
        }
    }

    // ---------------- PRICE ----------------
    private fun loadPrice(symbol: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = RetrofitClient.api.getTimeSeries(
                    symbol = symbol,
                    interval = "1min",
                    outputSize = 1,
                    apiKey = BuildConfig.TWELVE_API_KEY
                )

                val latest = result.values?.firstOrNull()
                val price = latest?.close?.toDoubleOrNull()

                requireActivity().runOnUiThread {
                    binding.tickerPrice.text =
                        if (price != null) "$${"%.4f".format(price)}"
                        else "Price unavailable"
                }

            } catch (e: Exception) {
                e.printStackTrace()
                requireActivity().runOnUiThread {
                    binding.tickerPrice.text = "Error loading price"
                }
            }
        }
    }

    // ---------------- CANDLE DATA ----------------
    private fun loadCandleData(symbol: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.api.getTimeSeries(
                    symbol = symbol,
                    interval = "1h",
                    outputSize = 50,
                    apiKey = BuildConfig.TWELVE_API_KEY
                )

                val values = response.values ?: emptyList()
                if (values.isEmpty()) {
                    requireActivity().runOnUiThread {
                        binding.tickerCandleChart.setNoDataText("No chart data")
                        binding.tickerCandleChart.invalidate()
                    }
                    return@launch
                }

                val entries = ArrayList<CandleEntry>()
                var index = 0f

                for (v in values.reversed()) {
                    entries.add(
                        CandleEntry(
                            index,
                            v.high.toFloat(),
                            v.low.toFloat(),
                            v.open.toFloat(),
                            v.close.toFloat()
                        )
                    )
                    index += 1f
                }

                val set = CandleDataSet(entries, "Price").apply {
                    decreasingColor = Color.RED
                    decreasingPaintStyle = Paint.Style.FILL
                    increasingColor = Color.GREEN
                    increasingPaintStyle = Paint.Style.FILL
                    neutralColor = Color.WHITE
                    setDrawValues(false)
                    shadowColorSameAsCandle = true
                }

                val data = CandleData(set)

                requireActivity().runOnUiThread {
                    binding.tickerCandleChart.data = data
                    binding.tickerCandleChart.description.isEnabled = false
                    binding.tickerCandleChart.legend.isEnabled = false
                    binding.tickerCandleChart.axisRight.isEnabled = false

                    binding.tickerCandleChart.axisLeft.textColor = Color.WHITE
                    binding.tickerCandleChart.xAxis.textColor = Color.WHITE
                    binding.tickerCandleChart.xAxis.position = XAxis.XAxisPosition.BOTTOM

                    binding.tickerCandleChart.invalidate()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                requireActivity().runOnUiThread {
                    binding.tickerCandleChart.setNoDataText("Failed to load chart")
                }
            }
        }
    }

    private fun openTradeScreen(symbol: String, action: String) {
        val bundle = Bundle().apply {
            putString("symbol", symbol)
            putString("action", action)
        }
        findNavController().navigate(R.id.tradeFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}