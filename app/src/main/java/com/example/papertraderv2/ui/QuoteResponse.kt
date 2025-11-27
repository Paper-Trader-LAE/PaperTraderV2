package com.example.papertraderv2.ui

data class QuoteResponse(
    val symbol: String?,
    val price: String?,
    val percent_change: String?,
    val change: String?
)
