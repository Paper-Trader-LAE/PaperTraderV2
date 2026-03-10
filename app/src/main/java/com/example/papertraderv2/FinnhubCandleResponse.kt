package com.example.papertraderv2

data class FinnhubCandleResponse(
    val c: List<Double>?, // close
    val h: List<Double>?, // high
    val l: List<Double>?, // low
    val o: List<Double>?, // open
    val t: List<Long>?,   // timestamps
    val s: String?        // status
)