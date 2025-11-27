package com.example.papertraderv2.ui

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.papertraderv2.R

class SearchFragment : Fragment() {

    private lateinit var searchInput: EditText
    private lateinit var recentContainer: LinearLayout
    private lateinit var categoriesContainer: LinearLayout

    private val maxRecent = 5

    private val categories = mapOf(
        "Indices" to listOf("S&P 500", "Dow Jones", "Nasdaq 100"),
        "Forex Pairs" to listOf("EUR/USD", "GBP/USD", "USD/JPY"),
        "Companies" to listOf("Apple", "Tesla", "Amazon"),
        "Crypto" to listOf("Bitcoin", "Ethereum", "Solana"),
        "Oils / Commodities" to listOf("Brent Oil", "Gold", "Silver")
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        searchInput = view.findViewById(R.id.searchInput)
        recentContainer = view.findViewById(R.id.recentContainer)
        categoriesContainer = view.findViewById(R.id.categoriesContainer)

        setupCategories()
        loadRecentSearches()

        searchInput.setOnEditorActionListener { _, _, _ ->
            val text = searchInput.text.toString().trim()
            if (text.isNotEmpty()) {
                saveRecentSearch(text)
                openTicker(text)
            }
            true
        }

        return view
    }

    private fun setupCategories() {
        categories.forEach { (title, items) ->

            val categoryTitle = TextView(requireContext()).apply {
                text = title
                setTextColor(resources.getColor(android.R.color.white))
                textSize = 16f
                setPadding(0, 15, 0, 10)
            }

            categoriesContainer.addView(categoryTitle)

            items.forEach { item ->
                val button = Button(requireContext()).apply {
                    text = item
                    setBackgroundColor(0xFF1B1B1B.toInt())
                    setTextColor(0xFFFFFFFF.toInt())
                    setPadding(20, 20, 20, 20)
                }

                button.setOnClickListener { openTicker(item) }
                categoriesContainer.addView(button)
            }
        }
    }

    private fun saveRecentSearch(search: String) {
        val prefs = requireContext().getSharedPreferences("searches", Context.MODE_PRIVATE)
        val list = prefs.getStringSet("recent", LinkedHashSet())!!.toMutableList()

        list.remove(search)
        list.add(0, search)

        if (list.size > maxRecent)
            list.removeLast()

        prefs.edit().putStringSet("recent", list.toSet()).apply()

        loadRecentSearches()
    }

    private fun loadRecentSearches() {
        recentContainer.removeAllViews()

        val prefs = requireContext().getSharedPreferences("searches", Context.MODE_PRIVATE)
        val list = prefs.getStringSet("recent", emptySet())!!.toList()

        list.take(maxRecent).forEach { item ->
            val btn = Button(requireContext()).apply {
                text = item
                setBackgroundColor(0xFF1B1B1B.toInt())
                setTextColor(0xFFFFFFFF.toInt())
            }

            btn.setOnClickListener { openTicker(item) }

            recentContainer.addView(btn)
        }
    }

    private fun openTicker(ticker: String) {
        val bundle = Bundle()
        bundle.putString("ticker", ticker)

        findNavController().navigate(R.id.tickerDetailsFragment, bundle)
    }
}