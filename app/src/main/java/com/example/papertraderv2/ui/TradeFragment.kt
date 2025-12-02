package com.example.papertraderv2.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.papertraderv2.BuildConfig
import com.example.papertraderv2.RetrofitClient
import com.example.papertraderv2.data.AppDatabase
import com.example.papertraderv2.databinding.FragmentTradeBinding
import com.example.papertraderv2.models.Trade
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TradeFragment : Fragment() {

    private var _binding: FragmentTradeBinding? = null
    private val binding get() = _binding!!

    private var selectedCategory = "Stocks"
    private var selectedSymbol = ""
    private var livePrice = 0.0

    private val stocks = listOf("AAPL", "MSFT", "TSLA", "AMZN")
    private val forex = listOf("EURUSD", "GBPUSD", "USDJPY", "AUDUSD")
    private val crypto = listOf("BTCUSD", "ETHUSD", "ADAUSD", "XRPUSD")

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
        setupSymbolSpinner()

        binding.inputQuantity.addTextChangedListener(quantityWatcher)

        binding.btnBuy.setOnClickListener { submitTrade("Buy") }
        binding.btnSell.setOnClickListener { submitTrade("Sell") }
    }

    // -------------------------
    // CATEGORY SPINNER
    // -------------------------
    private fun setupCategorySpinner() {
        val items = listOf("Stocks", "Forex", "Crypto")

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            items
        )

        binding.spinnerCategory.adapter = adapter

        binding.spinnerCategory.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedCategory = items[position]
                    updateSymbolSpinner()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    // -------------------------
    // SYMBOL SPINNER
    // -------------------------
    private fun updateSymbolSpinner() {
        val list = when (selectedCategory) {
            "Stocks" -> stocks
            "Forex" -> forex
            "Crypto" -> crypto
            else -> stocks
        }

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            list
        )

        binding.spinnerSymbol.adapter = adapter

        binding.spinnerSymbol.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedSymbol = list[position]
                    fetchLivePrice(selectedSymbol)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    private fun setupSymbolSpinner() {
        updateSymbolSpinner()
    }

    // -------------------------
    // LIVE PRICE API CALL
    // -------------------------
    private fun fetchLivePrice(symbol: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.api.getTimeSeries(
                    symbol = symbol,
                    interval = "1min",
                    outputSize = 1,
                    apiKey = BuildConfig.TWELVE_API_KEY
                )

                val latest = response.values?.firstOrNull()
                livePrice = latest?.close?.toDoubleOrNull() ?: 0.0

                requireActivity().runOnUiThread {
                    binding.textLivePrice.text = "$${"%.4f".format(livePrice)}"
                    updateEstimatedCost()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // -------------------------
    // QUANTITY LISTENER
    // -------------------------
    private val quantityWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            updateEstimatedCost()
        }
        override fun afterTextChanged(s: Editable?) {}
    }

    private fun updateEstimatedCost() {
        val qty = binding.inputQuantity.text.toString().toDoubleOrNull() ?: 0.0
        val total = qty * livePrice

        binding.textEstimated.text = "Estimated: $${"%.2f".format(total)}"
    }

    // -------------------------
    // SUBMIT TRADE
    // -------------------------
    private fun submitTrade(action: String) {
        val qty = binding.inputQuantity.text.toString().toDoubleOrNull() ?: return

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
                Toast.makeText(requireContext(), "Trade placed!", Toast.LENGTH_SHORT).show()

                // Notify HomeFragment to reload
                parentFragmentManager.setFragmentResult("trade_made", Bundle())

                // Go back
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}