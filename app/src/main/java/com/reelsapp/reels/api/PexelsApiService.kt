package com.reelsapp.reels.api

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface PexelsApiService {
    @GET("videos/search")
    suspend fun searchVideos(
        @Header("Authorization") apiKey: String,
        @Query("query") query: String = "sports",
        @Query("orientation") orientation: String = "portrait",
        @Query("per_page") perPage: Int = 15
    ): PexelsResponse

    @GET("videos/popular")
    suspend fun getPopularVideos(
        @Header("Authorization") apiKey: String,
        @Query("orientation") orientation: String = "portrait",
        @Query("per_page") perPage: Int = 15
    ): PexelsResponse
}
