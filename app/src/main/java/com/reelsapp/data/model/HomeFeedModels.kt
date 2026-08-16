package com.reelsapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UnsplashImage(
    val id: String,
    val description: String? = null,
    @SerialName("alt_description")
    val altDescription: String? = null,
    val urls: UnsplashUrls,
    val user: UnsplashUser,
    val likes: Int = 0,
    val width: Int = 1080,
    val height: Int = 1920
)

@Serializable
data class UnsplashUrls(
    val raw: String,
    val full: String,
    val regular: String,
    val small: String,
    val thumb: String
)

@Serializable
data class UnsplashUser(
    val id: String,
    val username: String,
    val name: String,
    @SerialName("profile_image")
    val profileImage: UnsplashProfileImage? = null
)

@Serializable
data class UnsplashProfileImage(
    val small: String,
    val medium: String,
    val large: String
)

// Paging 3 Item Model for Home Cards
data class HomeFeedCard(
    val id: String,
    val title: String,
    val author: String,
    val imageUrl: String,
    val avatarUrl: String,
    val category: String,
    val likesCount: Int
)
