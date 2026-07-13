package com.example.brickcollector.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.brickcollector.data.LegoResponse

@Dao
interface LegoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSet(set: LegoResponse): Long

    @Delete
    suspend fun deleteSet(set: LegoResponse): Int

    @Query("DELETE FROM legos WHERE set_num = :legoId")
    suspend fun deleteSetById(legoId: String): Int

    @Query("DELETE FROM legos")
    suspend fun deleteAllSets(): Int

    @Query("SELECT DISTINCT theme_id FROM legos")
    suspend fun getSavedThemeIds(): List<Int>

    @Query("SELECT * FROM legos WHERE theme_id = :themeId")
    suspend fun getLegosByTheme(themeId: Int): List<LegoResponse>

    @Query("SELECT COUNT(*) FROM legos")
    suspend fun getTotalSets(): Int

    @Query("SELECT SUM(num_parts) FROM legos")
    suspend fun getTotalPieces(): Int

}
