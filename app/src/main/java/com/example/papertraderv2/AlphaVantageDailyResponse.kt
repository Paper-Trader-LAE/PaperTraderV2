package com.example.papertraderv2

import com.google.gson.annotations.SerializedName

data class AlphaVantageDailyResponse(
    @SerializedName("Meta Data")
    val metaData: Map<String, String>? = null,

    @SerializedName("Time Series (Daily)")
    val timeSeriesDaily: Map<String, AlphaVantageDailyCandle>? = null,

    @SerializedName("Note")
    val note: String? = null,

    @SerializedName("Error Message")
    val errorMessage: String? = null
)

data class AlphaVantageDailyCandle(
    @SerializedName("1. open")
    val open: String? = null,

    @SerializedName("2. high")
    val high: String? = null,

    @SerializedName("3. low")
    val low: String? = null,

    @SerializedName("4. close")
    val close: String? = null,

    @SerializedName("5. volume")
    val volume: String? = null
)