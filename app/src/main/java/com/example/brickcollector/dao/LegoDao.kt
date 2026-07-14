package com.example.brickcollector.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.brickcollector.data.LegoResponse
import com.example.brickcollector.data.Theme

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

    @Query("SELECT DISTINCT theme_id FROM legos WHERE isWishlist = 0")
    suspend fun getSavedThemeIds(): List<Int>

    @Query("SELECT * FROM legos WHERE theme_id = :themeId AND isWishlist = 0")
    suspend fun getLegosByTheme(themeId: Int): List<LegoResponse>

    @Query("SELECT COUNT(*) FROM legos WHERE isWishlist = 0")
    suspend fun getTotalSets(): Int

    @Query("SELECT SUM(num_parts) FROM legos WHERE isWishlist = 0")
    suspend fun getTotalPieces(): Int

    @Query("SELECT set_num FROM legos WHERE isWishlist = 0")
    suspend fun getSavedSetNums(): List<String>

    @Query("SELECT * FROM legos WHERE isWishlist = 0")
    suspend fun getAllLegos(): List<LegoResponse>

    @Query("SELECT * FROM legos WHERE set_num = :setNum LIMIT 1")
    suspend fun getLegoByNum(setNum: String): LegoResponse?

    @Query("SELECT DISTINCT theme_id FROM legos WHERE isWishlist = 1")
    suspend fun getWishlistThemeIds(): List<Int>

    @Query("SELECT * FROM legos WHERE theme_id = :themeId AND isWishlist = 1")
    suspend fun getWishlistLegosByTheme(themeId: Int): List<LegoResponse>

    @Query("SELECT set_num FROM legos WHERE isWishlist = 1")
    suspend fun getWishlistSetNums(): List<String>

    @Query("SELECT * FROM legos WHERE isWishlist = 1")
    suspend fun getWishlistLegos(): List<LegoResponse>

    @Query("UPDATE legos SET isWishlist = 0 WHERE set_num = :setNum")
    suspend fun markAsOwned(setNum: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThemes(themes: List<Theme>)

    @Query("SELECT * FROM themes")
    suspend fun getAllThemes(): List<Theme>

    @Query("SELECT COUNT(*) FROM themes")
    suspend fun getThemesCount(): Int
}
