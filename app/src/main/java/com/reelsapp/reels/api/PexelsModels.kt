package com.reelsapp.reels.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PexelsResponse(
    val page: Int = 1,
    @SerialName("per_page") val perPage: Int = 15,
    val videos: List<PexelsVideo> = emptyList()
)

@Serializable
data class PexelsVideo(
    val id: Long,
    val width: Int,
    val height: Int,
    val duration: Int,
    val image: String,
    val user: PexelsUser,
    @SerialName("video_files") val videoFiles: List<PexelsVideoFile> = emptyList()
)

@Serializable
data class PexelsUser(
    val id: Long,
    val name: String,
    val url: String
)

@Serializable
data class PexelsVideoFile(
    val id: Long,
    val quality: String,
    @SerialName("file_type") val fileType: String,
    val width: Int? = null,
    val height: Int? = null,
    val link: String
)
