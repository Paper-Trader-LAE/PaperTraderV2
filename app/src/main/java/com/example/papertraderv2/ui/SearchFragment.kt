package com.example.papertraderv2.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.papertraderv2.BuildConfig
import com.example.papertraderv2.R
import com.example.papertraderv2.RetrofitClient
import com.example.papertraderv2.adapters.SearchAdapter
import com.example.papertraderv2.databinding.FragmentSearchBinding
import com.example.papertraderv2.models.Stock
import com.example.papertraderv2.utils.SymbolMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: SearchAdapter
    private val results = mutableListOf<Stock>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        binding.searchRecycler.layoutManager = LinearLayoutManager(requireContext())

        adapter = SearchAdapter(results) { stock ->
            val bundle = Bundle().apply {
                putString("symbol", stock.symbol)
            }
            findNavController().navigate(R.id.tradeFragment, bundle)
        }

        binding.searchRecycler.adapter = adapter
        setupSearchBox()

        return binding.root
    }

    private fun setupSearchBox() {
        binding.searchInput.setOnEditorActionListener { v, _, _ ->
            val rawQuery = v.text.toString().trim()
            if (rawQuery.isNotEmpty()) {
                val cleaned = normalizeQuery(rawQuery)
                searchSymbol(cleaned, rawQuery)
            }
            true
        }
    }

    private fun normalizeQuery(query: String): String {
        return SymbolMapper.toSymbol(query).uppercase()
    }

    private fun searchSymbol(symbol: String, displayName: String) {
        results.clear()
        adapter.notifyDataSetChanged()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.api.getQuote(
                    symbol = symbol,
                    token = BuildConfig.FINNHUB_API_KEY
                )

                val price = response.c ?: 0.0

                if (price > 0.0) {
                    val stock = Stock(
                        name = displayName.uppercase(),
                        symbol = symbol.uppercase(),
                        price = price
                    )

                    requireActivity().runOnUiThread {
                        results.add(stock)
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    showNotFound(displayName)
                }
            } catch (e: Exception) {
                showNotFound(displayName)
            }
        }
    }

    private fun showNotFound(query: String) {
        requireActivity().runOnUiThread {
            results.clear()
            results.add(
                Stock(
                    name = "Not Found",
                    symbol = query.uppercase(),
                    price = 0.0
                )
            )
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}