package com.example.papertraderv2

data class TimeSeriesResponse(
    val values: List<TimeValue>?
)

data class TimeValue(
    val datetime: String,
    val open: String,
    val high: String,
    val low: String,
    val close: String
)