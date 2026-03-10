package com.example.papertraderv2

import com.example.papertraderv2.network.AlphaVantageApi
import com.example.papertraderv2.network.FinnhubApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val FINNHUB_BASE_URL = "https://finnhub.io/api/v1/"
    private const val ALPHA_BASE_URL = "https://www.alphavantage.co/"

    val api: FinnhubApi by lazy {
        Retrofit.Builder()
            .baseUrl(FINNHUB_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FinnhubApi::class.java)
    }

    val alphaApi: AlphaVantageApi by lazy {
        Retrofit.Builder()
            .baseUrl(ALPHA_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AlphaVantageApi::class.java)
    }
}