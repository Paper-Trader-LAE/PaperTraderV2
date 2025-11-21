package com.example.papertraderv2

import retrofit2.http.GET
import retrofit2.http.Query

interface StockApi {
    @GET("price")
    suspend fun getPrice(
        @Query("apikey") apikey: String,
        @Query("symbol") symbol: String
    ): PriceResponse
}
