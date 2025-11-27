package com.example.papertraderv2

data class SearchResponse(
    val data: List<SearchResult>?
)

data class SearchResult(
    val symbol: String?,
    val instrument_name: String?,
    val type: String?
)