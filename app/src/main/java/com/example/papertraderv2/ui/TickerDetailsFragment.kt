package com.example.papertraderv2.ui

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        displayName = arguments?.getString("displayName") ?: ""
        symbol = arguments?.getString("symbol") ?: ""

        binding.tickerTitle.text = if (displayName.isNotBlank()) displayName else symbol

        loadPrice(symbol)
        loadCandleData(symbol)

        binding.btnBuy.setOnClickListener { openTradeScreen(symbol, "Buy") }
        binding.btnSell.setOnClickListener { openTradeScreen(symbol, "Sell") }

        binding.btnWatchlist.setOnClickListener {
            Toast.makeText(requireContext(), "$symbol added to watchlist", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadPrice(symbol: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.api.getQuote(
                    symbol = symbol,
                    token = BuildConfig.FINNHUB_API_KEY
                )

                val price = response.c ?: 0.0

                requireActivity().runOnUiThread {
                    binding.tickerPrice.text =
                        if (price > 0.0) "$${"%.4f".format(price)}"
                        else "Price unavailable"
                }
            } catch (e: Exception) {
                requireActivity().runOnUiThread {
                    binding.tickerPrice.text = "Error loading price"
                }
            }
        }
    }

    private fun loadCandleData(symbol: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val to = System.currentTimeMillis() / 1000
                val from = to - (60 * 60 * 24 * 7)

                val response = RetrofitClient.api.getStockCandles(
                    symbol = symbol,
                    resolution = "60",
                    from = from,
                    to = to,
                    token = BuildConfig.FINNHUB_API_KEY
                )

                if (response.s != "ok" ||
                    response.o.isNullOrEmpty() ||
                    response.h.isNullOrEmpty() ||
                    response.l.isNullOrEmpty() ||
                    response.c.isNullOrEmpty()
                ) {
                    requireActivity().runOnUiThread {
                        binding.tickerCandleChart.setNoDataText("No chart data")
                        binding.tickerCandleChart.invalidate()
                    }
                    return@launch
                }

                val entries = ArrayList<CandleEntry>()
                for (i in response.c.indices) {
                    entries.add(
                        CandleEntry(
                            i.toFloat(),
                            response.h[i].toFloat(),
                            response.l[i].toFloat(),
                            response.o[i].toFloat(),
                            response.c[i].toFloat()
                        )
                    )
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