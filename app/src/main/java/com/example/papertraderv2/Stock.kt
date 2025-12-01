package com.example.papertraderv2.models

data class Stock(
    val name: String,
    val symbol: String,
    val price: Double,
    val quantity: Double = 0.0      // used on Home for "Your Stocks"
)