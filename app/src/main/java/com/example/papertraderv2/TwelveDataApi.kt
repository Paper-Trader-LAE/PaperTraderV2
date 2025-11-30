// com/example/papertraderv2/network/TwelveDataApi.kt
package com.example.papertraderv2.network

import com.example.papertraderv2.models.PriceResponse
import com.example.papertraderv2.TimeSeriesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TwelveDataApi {

    @GET("price")
    suspend fun getPrice(
        @Query("symbol") symbol: String,
        @Query("apikey") apiKey: String
    ): PriceResponse

    @GET("time_series")
    suspend fun getTimeSeries(
        @Query("symbol") symbol: String,
        @Query("interval") interval: String = "5min",
        @Query("outputsize") outputSize: Int = 80,
        @Query("apikey") apiKey: String
    ): TimeSeriesResponse
}