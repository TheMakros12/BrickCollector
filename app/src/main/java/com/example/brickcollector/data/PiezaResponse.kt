package com.example.brickcollector.data

data class PiezasResponse(
    val count: Int,
    val results: List<PiezaResponse>
)

data class PiezaResponse(
    val part: Part,
    val quantity: Int
)

data class Part(
    val part_num: String,
    val part_img_url: String
)
