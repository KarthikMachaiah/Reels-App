package com.reelsapp.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.reelsapp.data.model.HomeFeedCard
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.reelsapp.reels.ComposeReelsFeed
import com.reelsapp.reels.DummyReelsData
import com.reelsapp.ui.home.HomeViewModel
import com.reelsapp.ui.navigation.AppTab
import com.reelsapp.ui.navigation.FloatingPillNavigationBar
import com.reelsapp.ui.theme.BrandEmerald
import com.reelsapp.ui.theme.BrandEmeraldLight

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

    // Intercept back button
    BackHandler(enabled = currentTab != AppTab.HOME || state.isReelsFeedActive || isAiReelActive) {
        when {
            state.isReelsFeedActive -> viewModel.closeReelsFeed()
            isAiReelActive -> isAiReelActive = false
            else -> currentTab = AppTab.HOME
        }
    }

    // Floating App Bar is visible on Home Dashboard, Profile, and AI Reels (unless generated video is playing)
    val isAppNavVisible = when (currentTab) {
        AppTab.HOME -> !state.isReelsFeedActive
        AppTab.JUST_REELS -> false
        AppTab.AI_IMAGE -> !isAiReelActive
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
                    // Home Dashboard Screen (Welcome card & Open Reels launcher)
                    HomeDashboardContent(
                        isReelsFeedActive = state.isReelsFeedActive,
                        isReelsDialogVisible = state.isReelsDialogVisible,
                        reels = if (state.reels.isNotEmpty()) state.reels else DummyReelsData.sampleReels,
                        windowSizeClass = windowSizeClass,
                        onOpenReelsClick = { viewModel.showReelsDialog() },
                        onDismissDialog = { viewModel.dismissReelsDialog() },
                        onConfirmLaunchReels = { viewModel.launchReelsFeed() },
                        onCloseReelsFeed = { viewModel.closeReelsFeed() }
                    )
                }
                AppTab.AI_IMAGE -> {
                    // Dedicated AI Image Generator Page
                    AiImageScreen(
                        onReelsPlaybackStateChanged = { isPlaying ->
                            isAiReelActive = isPlaying
                        }
                    )
                }
                AppTab.JUST_REELS -> {
                    // Dedicated Full-Screen Original Reels Feed Page
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

        // Floating Glassmorphism Animated Pill Navigation Bar
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

@Composable
private fun HomeDashboardContent(
    isReelsFeedActive: Boolean,
    isReelsDialogVisible: Boolean,
    reels: List<com.reelsapp.reels.ReelItem>,
    windowSizeClass: WindowSizeClass?,
    onOpenReelsClick: () -> Unit,
    onDismissDialog: () -> Unit,
    onConfirmLaunchReels: () -> Unit,
    onCloseReelsFeed: () -> Unit
) {
    val isExpanded = windowSizeClass?.widthSizeClass == WindowWidthSizeClass.Expanded

    if (isReelsFeedActive) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            ComposeReelsFeed(
                reels = reels,
                onBackClick = onCloseReelsFeed,
                modifier = if (isExpanded) Modifier.widthIn(max = 500.dp).fillMaxHeight() else Modifier.fillMaxSize()
            )
        }
    } else {
        val pagingFlow = remember {
            Pager(
                config = PagingConfig(
                    pageSize = 10,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = { com.reelsapp.data.paging.HomeFeedPagingSource() }
            ).flow
        }
        val lazyPagingItems = pagingFlow.collectAsLazyPagingItems()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 28.dp,
                bottom = 100.dp
            )
        ) {
            item {
                OpenReelsHeroCard(onClick = onOpenReelsClick)
                Spacer(Modifier.height(28.dp))
                Text(
                    text = "Live Internet Feed Cards (Cursor Paginated)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 14.dp)
                )
            }

            items(
                count = lazyPagingItems.itemCount,
                key = lazyPagingItems.itemKey { card: HomeFeedCard -> card.id }
            ) { index ->
                val cardItem = lazyPagingItems[index]
                if (cardItem != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // High Resolution Remote Cover Image from Internet
                            coil.compose.AsyncImage(
                                model = cardItem.imageUrl,
                                contentDescription = cardItem.title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )

                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    coil.compose.AsyncImage(
                                        model = cardItem.avatarUrl,
                                        contentDescription = cardItem.author,
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = cardItem.author,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = cardItem.category,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = BrandEmerald
                                        )
                                    }
                                }
                                Spacer(Modifier.height(10.dp))
                                Text(
                                    text = cardItem.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = "❤️ ${cardItem.likesCount} likes • Loaded via Cursor Pagination",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Append Loading Indicator state
            if (lazyPagingItems.loadState.append is androidx.paging.LoadState.Loading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material3.CircularProgressIndicator(
                            color = BrandEmerald,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }

        // Animated Bouncy Alert Dialog
        AnimatedVisibility(
            visible = isReelsDialogVisible,
            enter = fadeIn(tween(250)),
            exit = fadeOut(tween(200))
        ) {
            ReelsDialog(
                onDismiss = onDismissDialog,
                onConfirm = onConfirmLaunchReels
            )
        }
    }
}

@Composable
private fun OpenReelsHeroCard(onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            BrandEmerald.copy(alpha = 0.2f),
                            BrandEmeraldLight.copy(alpha = 0.05f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.PlayCircle,
                contentDescription = null,
                tint = BrandEmerald,
                modifier = Modifier.size(60.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Welcome to Reels",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Discover short video stories & AI generated reels",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth(0.65f)
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandEmerald,
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 8.dp,
                pressedElevation = 2.dp
            )
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Open Reels Feed",
                style = MaterialTheme.typography.labelLarge,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ReelsDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f))
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = true,
                enter = scaleIn(
                    initialScale = 0.5f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + fadeIn(tween(250)),
                exit = scaleOut(
                    targetScale = 0.5f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + slideOutVertically(tween(180)) + fadeOut(tween(180))
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 420.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(BrandEmerald, BrandEmeraldLight)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PlayCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(44.dp)
                            )
                        }

                        Spacer(Modifier.height(20.dp))

                        Text(
                            text = "Open Reels Feed",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = "Get ready to dive into your personalized short video feed — swipe, discover, and enjoy!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )

                        Spacer(Modifier.height(28.dp))

                        Button(
                            onClick = {
                                onDismiss()
                                onConfirm()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(26.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandEmerald,
                                contentColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                        ) {
                            Text(
                                text = "Let's Go!",
                                style = MaterialTheme.typography.labelLarge,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        TextButton(onClick = onDismiss) {
                            Text(
                                text = "Not now",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
