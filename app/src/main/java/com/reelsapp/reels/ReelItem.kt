package com.reelsapp.reels

import com.reelsapp.R

data class ReelItem(
    val id: String,
    val videoUrl: String,
    val thumbnailUrl: String,
    val title: String,
    val username: String,
    val userAvatar: Int = R.drawable.karthik_avatar,
    val likesCount: String,
    val commentsCount: String,
    val isLiked: Boolean = false,
    val isAiImage: Boolean = false
)
