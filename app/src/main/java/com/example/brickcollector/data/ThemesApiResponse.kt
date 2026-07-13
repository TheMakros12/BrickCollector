package com.example.brickcollector.data

data class ThemesApiResponse(
    val results: List<Theme>
)

data class Theme(
    val id: Int,
    val name: String
)
