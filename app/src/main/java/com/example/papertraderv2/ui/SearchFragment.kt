// com/example/papertraderv2/ui/SearchFragment.kt
package com.example.papertraderv2.ui

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.papertraderv2.R
import com.example.papertraderv2.utils.SymbolMapper

class SearchFragment : Fragment() {

    data class SearchItem(
        val label: String,   // what user sees
        val symbol: String   // what API needs
    )

    private lateinit var searchInput: EditText
    private lateinit var recentContainer: LinearLayout
    private lateinit var categoriesContainer: LinearLayout

    private val maxRecent = 5
    private val prefsName = "search_preferences"
    private val recentKey = "recent_searches"

    private val categories: Map<String, List<SearchItem>> = mapOf(
        "Indices" to listOf(
            SearchItem("S&P 500", "SPX"),
            SearchItem("Dow Jones", "DJI"),
            SearchItem("Nasdaq 100", "NDX")
        ),
        "Forex Pairs" to listOf(
            SearchItem("EUR/USD", "EURUSD"),
            SearchItem("GBP/USD", "GBPUSD"),
            SearchItem("USD/JPY", "USDJPY")
        ),
        "Companies" to listOf(
            SearchItem("Apple", "AAPL"),
            SearchItem("Tesla", "TSLA"),
            SearchItem("Amazon", "AMZN")
        ),
        "Crypto" to listOf(
            SearchItem("Bitcoin", "BTCUSD"),
            SearchItem("Ethereum", "ETHUSD"),
            SearchItem("Solana", "SOLUSD")
        ),
        "Oils / Commodities" to listOf(
            SearchItem("Brent Oil", "BRENT"),
            SearchItem("Gold", "XAUUSD"),
            SearchItem("Silver", "XAGUSD")
        )
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
            if (text.isNotBlank()) {
                handleSearchText(text)
            }
            true
        }

        return view
    }

    // ---------------- HANDLE FREE TEXT ----------------
    private fun handleSearchText(text: String) {
        val normalized = text.trim()

        // 1) Try match against known category labels or symbols
        val flatList = categories.values.flatten()
        val match = flatList.find {
            it.label.equals(normalized, ignoreCase = true) ||
                    it.symbol.equals(
                        normalized.replace(" ", "").replace("/", ""),
                        ignoreCase = true
                    )
        }

        val item = if (match != null) {
            println("🔎 Search match → label='${match.label}', symbol='${match.symbol}'")
            match
        } else {
            // 2) Use SymbolMapper to guess the API symbol
            val mappedSymbol = SymbolMapper.toSymbol(normalized)
            println("🔎 Search free text → '$normalized' mapped to symbol '$mappedSymbol'")
            SearchItem(
                label = normalized,
                symbol = mappedSymbol
            )
        }

        saveRecentSearch(item.label)
        openTicker(item)
    }

    // ---------------- CATEGORIES ----------------
    private fun setupCategories() {
        categories.forEach { (title, items) ->

            val categoryTitle = TextView(requireContext()).apply {
                text = title
                setTextColor(resources.getColor(android.R.color.white))
                textSize = 17f
                setPadding(0, 20, 0, 12)
            }

            categoriesContainer.addView(categoryTitle)

            items.forEach { item ->
                val itemBtn = createItemButton(item.label)
                itemBtn.setOnClickListener { openTicker(item) }
                categoriesContainer.addView(itemBtn)
            }
        }
    }

    // ---------------- RECENT SEARCHES ----------------
    private fun saveRecentSearch(search: String) {
        val prefs = requireContext().getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        val list = prefs.getStringSet(recentKey, LinkedHashSet())!!.toMutableList()

        list.remove(search)
        list.add(0, search)

        val trimmed = list.take(maxRecent).toSet()
        prefs.edit().putStringSet(recentKey, trimmed).apply()

        loadRecentSearches()
    }

    private fun loadRecentSearches() {
        recentContainer.removeAllViews()

        val prefs = requireContext().getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        val list = prefs.getStringSet(recentKey, emptySet())!!.toList()

        list.forEach { label ->
            val btn = createItemButton(label)
            btn.setOnClickListener {
                handleSearchText(label)
            }
            recentContainer.addView(btn)
        }
    }

    // ---------------- BUTTON STYLE ----------------
    private fun createItemButton(text: String): Button {
        return Button(requireContext()).apply {
            this.text = text
            setBackgroundColor(0xFF1B1B1B.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 15f
            setPadding(25, 20, 25, 20)

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 10, 0, 0)
            layoutParams = params
        }
    }

    // ---------------- NAVIGATION ----------------
    private fun openTicker(item: SearchItem) {
        val bundle = Bundle().apply {
            putString("displayName", item.label)
            putString("symbol", item.symbol)
        }

        findNavController().navigate(
            R.id.tickerDetailsFragment,
            bundle,
            androidx.navigation.NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setPopUpTo(R.id.tickerDetailsFragment, true)
                .build()
        )
    }
}