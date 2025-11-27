package com.example.papertraderv2.network

import com.example.papertraderv2.PriceResponse
import com.example.papertraderv2.SearchResponse
import com.example.papertraderv2.TimeSeriesResponse
import com.example.papertraderv2.ui.QuoteResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TwelveDataApi {

    // ----- REAL-TIME PRICE -----
    @GET("price")
    suspend fun getPrice(
        @Query("symbol") symbol: String,
        @Query("apikey") apiKey: String
    ): PriceResponse

    // ----- REAL-TIME QUOTE (includes change %) -----
    @GET("quote")
    suspend fun getQuote(
        @Query("symbol") symbol: String,
        @Query("apikey") apiKey: String
    ): QuoteResponse

    // ----- TIME SERIES FOR CHART -----
    @GET("time_series")
    suspend fun getTimeSeries(
        @Query("symbol") symbol: String,
        @Query("interval") interval: String = "1day",
        @Query("outputsize") outputSize: Int = 30,
        @Query("apikey") apiKey: String
    ): TimeSeriesResponse

    // ----- SEARCH -----
    @GET("symbol_search")
    suspend fun searchSymbol(
        @Query("symbol") query: String,
        @Query("apikey") apiKey: String
    ): SearchResponse
}