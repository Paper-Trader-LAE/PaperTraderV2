// com/example/papertraderv2/utils/SymbolMapper.kt
package com.example.papertraderv2.utils

object SymbolMapper {

    private val map = mapOf(
        // Indices
        "S&P 500" to "SPY",
        "Dow Jones" to "DIA",
        "Nasdaq 100" to "QQQ",

        // Forex
        "EUR/USD" to "EURUSD",
        "GBP/USD" to "GBPUSD",
        "USD/JPY" to "USDJPY",

        // Crypto
        "Bitcoin" to "BTCUSD",
        "Ethereum" to "ETHUSD",
        "Solana" to "SOLUSD",

        // Commodities
        "Gold" to "XAUUSD",
        "Silver" to "XAGUSD",
        "Brent Oil" to "BRENT",

        // Stocks
        "Apple" to "AAPL",
        "Tesla" to "TSLA",
        "Amazon" to "AMZN"
    )

    fun toSymbol(input: String): String {
        val trimmed = input.trim()

        // 1) Exact label match (e.g. "Apple", "EUR/USD")
        map[trimmed]?.let { mapped ->
            println("🔁 SymbolMapper (label match) → '$trimmed' → '$mapped'")
            return mapped
        }

        // 2) Cleaned version (for stuff like "eur/usd", "eurusd", " btc usd ")
        val cleaned = trimmed.uppercase()
            .replace(" ", "")
            .replace("/", "")

        // If map has a key exactly equal to the cleaned string, use it
        map[cleaned]?.let { mapped ->
            println(" SymbolMapper (cleaned key) → '$cleaned' → '$mapped'")
            return mapped
        }

        println(" SymbolMapper (fallback cleaned) → '$trimmed' → '$cleaned'")
        return cleaned
    }
}