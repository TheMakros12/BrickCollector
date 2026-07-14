package com.example.brickcollector.api

import retrofit2.http.GET
import retrofit2.http.Query

interface BricksetApiServices {
    @GET("api/v3.asmx/getSets")
    suspend fun getSets(
        @Query("apiKey") apiKey: String,
        @Query("userHash") userHash: String = "",
        @Query("params") params: String
    ): BricksetResponse
}

data class BricksetResponse(
    val status: String,
    val matches: Int,
    val sets: List<BricksetSet>?
)

data class BricksetSet(
    val setID: Int,
    val number: String,
    val name: String,
    val LEGOCom: LegoComPrices?
)

data class LegoComPrices(
    val DE: RegionPrice?,
    val US: RegionPrice?,
    val UK: RegionPrice?
)

data class RegionPrice(
    val retailPrice: Double?
)
