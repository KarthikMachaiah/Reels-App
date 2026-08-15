package com.reelsapp.reels

import com.reelsapp.R

object DummyReelsData {
    private const val pkg = "com.reelsapp.debug"
    val sampleReels = listOf(
        ReelItem(
            id = "1",
            videoUrl = "android.resource://$pkg/${R.raw.reel_1}",
            thumbnailUrl = "https://images.unsplash.com/photo-1519766304817-4f37bda74a29?q=80&w=600",
            title = "Ocean waves & deep sea exploration 🌊🐋 #Nature",
            username = "karthik",
            userAvatar = R.drawable.karthik_avatar,
            likesCount = "34.5K",
            commentsCount = "1.8K"
        ),
        ReelItem(
            id = "2",
            videoUrl = "android.resource://$pkg/${R.raw.reel_2}",
            thumbnailUrl = "https://images.unsplash.com/photo-1534438327276-14e5300c3a48?q=80&w=600",
            title = "Bicycle rider & street action 🚲⚡ #Cycling",
            username = "karthik",
            userAvatar = R.drawable.karthik_avatar,
            likesCount = "52.9K",
            commentsCount = "2.4K"
        ),
        ReelItem(
            id = "3",
            videoUrl = "android.resource://$pkg/${R.raw.reel_3}",
            thumbnailUrl = "https://images.unsplash.com/photo-1546519638-68e109498ffc?q=80&w=600",
            title = "Store walkthrough & shopping vlog 🛒✨",
            username = "karthik",
            userAvatar = R.drawable.karthik_avatar,
            likesCount = "45.1K",
            commentsCount = "3.4K"
        ),
        ReelItem(
            id = "4",
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
