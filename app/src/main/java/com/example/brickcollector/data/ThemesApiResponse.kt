package com.example.brickcollector.data

import androidx.room.Entity
import androidx.room.PrimaryKey

data class ThemesApiResponse(
    val results: List<Theme>
)

@Entity(tableName = "themes")
data class Theme(
    @PrimaryKey val id: Int,
    val name: String,
    val parent_id: Int?
)
