package com.example.brickcollector.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.getValue

object RetrofitInstance {

    private const val BASE_URL = "https://rebrickable.com/api/v3/lego/"

    private const val API_KEY =
        "f138411743940f84bc3cd94fbdc27848"

    const val BRICKSET_API_KEY = "3-W4P3-9bW4"

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "key $API_KEY")
                .build()
            chain.proceed(request)
        }
        .build()

    val api: LegoApiServices by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LegoApiServices::class.java)
    }

    val bricksetApi: BricksetApiServices by lazy {
        Retrofit.Builder()
            .baseUrl("https://brickset.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BricksetApiServices::class.java)
    }

}
