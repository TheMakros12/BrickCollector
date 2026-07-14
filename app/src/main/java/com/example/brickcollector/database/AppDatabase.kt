package com.example.brickcollector.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.brickcollector.dao.LegoDao
import com.example.brickcollector.data.LegoResponse
import com.example.brickcollector.data.Theme

@Database(entities = [LegoResponse::class, Theme::class], version = 7)
abstract class AppDatabase : RoomDatabase() {
    abstract fun legoDao(): LegoDao
}
