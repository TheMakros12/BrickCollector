package com.example.brickcollector.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.brickcollector.dao.LegoDao
import com.example.brickcollector.data.LegoResponse

@Database(entities = [LegoResponse::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun legoDao(): LegoDao
}
