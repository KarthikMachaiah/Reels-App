package com.reelsapp.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.reelsapp.reels.ComposeReelsFeed
import com.reelsapp.reels.DummyReelsData
import com.reelsapp.ui.home.HomeViewModel
import com.reelsapp.ui.navigation.AppTab
import com.reelsapp.ui.navigation.FloatingPillNavigationBar

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = mavericksViewModel(),
    isDarkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {},
    windowSizeClass: WindowSizeClass? = null
) {
    val state by viewModel.collectAsState()
    var currentTab by remember { mutableStateOf(AppTab.HOME) }
    var isAiReelActive by remember { mutableStateOf(false) }

    // Intercept back button when on sub-tab, sub-reels, or AI reel player
    BackHandler(enabled = currentTab != AppTab.HOME || isAiReelActive) {
        if (isAiReelActive) {
            isAiReelActive = false
        } else {
            currentTab = AppTab.HOME
        }
    }

    // Floating App Bar is hidden when viewing full-screen reels on Home, Just Reels, or when generated AI Reel is playing
    val isAppNavVisible = when (currentTab) {
        AppTab.HOME -> false
        AppTab.JUST_REELS -> false
        AppTab.AI_REELS -> !isAiReelActive
        AppTab.PROFILE -> true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Tab Content with smooth Crossfade transition
        Crossfade(
            targetState = currentTab,
            label = "tabTransition",
            modifier = Modifier.fillMaxSize()
        ) { tab ->
            when (tab) {
                AppTab.HOME -> {
                    // Home Tab Full Screen Reels Feed
                    Box(modifier = Modifier.fillMaxSize()) {
                        ComposeReelsFeed(
                            reels = if (state.reels.isNotEmpty()) state.reels else DummyReelsData.sampleReels,
                            onBackClick = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                AppTab.AI_REELS -> {
                    // Dedicated AI Reels Generator Page
                    AiReelsScreen(
                        onReelsPlaybackStateChanged = { isPlaying ->
                            isAiReelActive = isPlaying
                        }
                    )
                }
                AppTab.JUST_REELS -> {
                    // Dedicated Original Reels Feed Page
                    Box(modifier = Modifier.fillMaxSize()) {
                        ComposeReelsFeed(
                            reels = if (state.reels.isNotEmpty()) state.reels else DummyReelsData.sampleReels,
                            onBackClick = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                AppTab.PROFILE -> {
                    // Profile Page with Light/Dark Theme Switcher
                    ProfileScreen(
                        isDarkTheme = isDarkTheme,
                        onToggleTheme = onToggleTheme
                    )
                }
            }
        }

        // Floating Glassmorphism Animated Pill Navigation Bar (Auto-Hides when reels are playing)
        AnimatedVisibility(
            visible = isAppNavVisible,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            FloatingPillNavigationBar(
                currentTab = currentTab,
                onTabSelected = { currentTab = it }
            )
        }
    }
}
