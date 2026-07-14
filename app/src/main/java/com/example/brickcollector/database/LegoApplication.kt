package com.example.brickcollector.database

import android.app.Application
import androidx.room.Room

class LegoApplication : Application() {

    companion object {
        lateinit var database: AppDatabase
    }

    override fun onCreate() {
        super.onCreate()
        
        val MIGRATION_5_6 = object : androidx.room.migration.Migration(5, 6) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE legos ADD COLUMN isBuilding INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE legos ADD COLUMN currentBag INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE legos ADD COLUMN totalBags INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_6_7 = object : androidx.room.migration.Migration(6, 7) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE legos ADD COLUMN startDate INTEGER")
                db.execSQL("ALTER TABLE legos ADD COLUMN endDate INTEGER")
            }
        }

        database = Room.databaseBuilder(this, AppDatabase::class.java, "AppDatabase")
            .addMigrations(MIGRATION_5_6, MIGRATION_6_7)
            .fallbackToDestructiveMigration()
            .build()
    }

}
