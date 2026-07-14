package com.example.brickcollector.data

import androidx.room.Entity
import androidx.room.PrimaryKey

data class LegoApiResponse(
    val count: Int,
    val results: List<LegoResponse>
)

@Entity(tableName = "legos")
data class LegoResponse(
    @PrimaryKey val set_num: String,
    val name: String,
    val theme_id: Int,
    val num_parts: Int,
    val set_img_url: String,
    val year: Int,
    val set_url: String,
    val isWishlist: Boolean = false,
    val retail_price: Double? = null,
    val isBuilding: Boolean = false,
    val currentBag: Int = 0,
    val totalBags: Int = 0,
    val startDate: Long? = null,
    val endDate: Long? = null
)
