package com.example.brickcollector.api

import com.example.brickcollector.data.LegoApiResponse
import com.example.brickcollector.data.LegoResponse
import com.example.brickcollector.data.PiezasResponse
import com.example.brickcollector.data.ThemesApiResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface LegoApiServices {

    @GET("themes/")
    suspend fun getThemes(@Query("limit") limit: Int = 1000): ThemesApiResponse

    @GET("sets/")
    suspend fun getSets(@Query("theme_id") themeId: String? = null,
                               @Query("search") search: String? = null,
                               @Query("page_size") pageSize: Int = 60,
                               @Query("ordering") order: String = "-year,-set_num"): LegoApiResponse

    @GET("sets/{set_num}/")
    suspend fun getLegoById(
        @Path("set_num") setNum: String
    ): LegoResponse

    @GET("sets/{set_num}/parts")
    suspend fun getPiezas(
        @Path("set_num") setNum: String,
        @Query("page_size") pageSize: Int = 1000
    ): PiezasResponse

}
