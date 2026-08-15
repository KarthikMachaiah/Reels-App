package com.reelsapp.reels.repository

import com.reelsapp.R
import com.reelsapp.reels.ReelItem
import com.reelsapp.reels.api.PexelsApiService
import com.reelsapp.reels.api.PexelsVideo
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

class ReelsRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    private val pexelsApi: PexelsApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.pexels.com/v1/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(PexelsApiService::class.java)
    }

    suspend fun getReels(apiKey: String? = null, query: String = "sports"): List<ReelItem> {
        if (!apiKey.isNullOrBlank()) {
            try {
                val response = pexelsApi.searchVideos(apiKey = apiKey, query = query)
                val mapped = response.videos.mapNotNull { it.toReelItem() }
                if (mapped.isNotEmpty()) return mapped
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return getFallbackReels()
    }

    private fun PexelsVideo.toReelItem(): ReelItem? {
        val bestFile = videoFiles.firstOrNull { it.quality == "hd" }
            ?: videoFiles.firstOrNull { it.quality == "sd" }
            ?: videoFiles.firstOrNull() ?: return null

        return ReelItem(
            id = id.toString(),
            videoUrl = bestFile.link,
            thumbnailUrl = image,
            title = "Awesome video by ${user.name} 🎬 #Reels",
            username = user.name.lowercase().replace(" ", "_"),
            userAvatar = R.drawable.karthik_avatar,
            likesCount = "${(10..99).random()}.${(1..9).random()}K",
            commentsCount = "${(100..999).random()}"
        )
    }

    private fun getFallbackReels(): List<ReelItem> {
        val pkg = "com.reelsapp.debug"
        return listOf(
            ReelItem(
                id = "f1",
                videoUrl = "android.resource://$pkg/${R.raw.reel_1}",
                thumbnailUrl = "https://images.unsplash.com/photo-1519766304817-4f37bda74a29?q=80&w=600",
                title = "Ocean waves & deep sea exploration 🌊🐋 #Nature",
                username = "karthik",
                userAvatar = R.drawable.karthik_avatar,
                likesCount = "34.5K",
                commentsCount = "1.8K"
            ),
            ReelItem(
                id = "f2",
                videoUrl = "android.resource://$pkg/${R.raw.reel_2}",
                thumbnailUrl = "https://images.unsplash.com/photo-1534438327276-14e5300c3a48?q=80&w=600",
                title = "Bicycle rider & street action 🚲⚡ #Cycling",
                username = "karthik",
                userAvatar = R.drawable.karthik_avatar,
                likesCount = "52.9K",
                commentsCount = "2.4K"
            ),
            ReelItem(
                id = "f3",
                videoUrl = "android.resource://$pkg/${R.raw.reel_3}",
                thumbnailUrl = "https://images.unsplash.com/photo-1546519638-68e109498ffc?q=80&w=600",
                title = "Store walkthrough & shopping vlog 🛒✨",
                username = "karthik",
                userAvatar = R.drawable.karthik_avatar,
                likesCount = "45.1K",
                commentsCount = "3.4K"
            ),
            ReelItem(
                id = "f4",
                videoUrl = "android.resource://$pkg/${R.raw.reel_4}",
                thumbnailUrl = "https://images.unsplash.com/photo-1517649763962-0c623266010b?q=80&w=600",
                title = "Face & gesture tracking demo 📹⚡",
                username = "karthik",
                userAvatar = R.drawable.karthik_avatar,
                likesCount = "12.8K",
                commentsCount = "620"
            )
        )
    }
}
