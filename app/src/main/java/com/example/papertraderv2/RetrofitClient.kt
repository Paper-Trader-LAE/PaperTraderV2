package com.example.papertraderv2

import com.example.papertraderv2.network.TwelveDataApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://api.twelvedata.com/"

    val api: TwelveDataApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TwelveDataApi::class.java)
    }
}