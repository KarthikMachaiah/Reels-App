package com.reelsapp.ui.home

import com.airbnb.mvrx.MavericksState
import com.reelsapp.reels.ReelItem

data class HomeState(
    val isReelsDialogVisible: Boolean = false,
    val isReelsFeedActive: Boolean = false,
    val reels: List<ReelItem> = emptyList(),
    val isLoadingReels: Boolean = false,
    val pexelsApiKey: String? = null
) : MavericksState
