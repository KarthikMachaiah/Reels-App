package com.reelsapp.reels

import android.content.Intent
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.reelsapp.R
import com.reelsapp.ui.theme.BrandEmerald
import com.reelsapp.ui.theme.MintSurface
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class CommentItem(
    val username: String,
    val text: String,
    val timeAgo: String,
    val likesCount: String
)

@OptIn(UnstableApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ComposeReelsFeed(
    reels: List<ReelItem>,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = { reels.size })
    val scope = rememberCoroutineScope()
    val player = remember { ReelPlayerManager.acquirePlayer(context) }

    val playerView = remember {
        PlayerView(context).apply {
            this.player = player
            useController = false
            keepScreenOn = true
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            setShutterBackgroundColor(android.graphics.Color.TRANSPARENT)
        }
    }

    var activePageIndex by remember { mutableStateOf(0) }
    var hasFirstFrameRendered by remember { mutableStateOf(false) }

    // Comments & Share Bottom Sheets state
    var showCommentsSheet by remember { mutableStateOf(false) }
    var showShareSheet by remember { mutableStateOf(false) }

    BackHandler(enabled = showCommentsSheet || showShareSheet || onBackClick != null) {
        when {
            showCommentsSheet -> showCommentsSheet = false
            showShareSheet -> showShareSheet = false
            else -> onBackClick?.invoke()
        }
    }

    LaunchedEffect(pagerState, reels) {
        snapshotFlow { pagerState.currentPage }
            .collect { page ->
                activePageIndex = page
                hasFirstFrameRendered = false
                val item = reels.getOrNull(page)
                if (item != null && !item.isAiImage) {
                    ReelPlayerManager.prepareMedia(context, player, item.videoUrl)
                } else if (item != null && item.isAiImage) {
                    player.pause()
                }
            }
    }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onRenderedFirstFrame() {
                hasFirstFrameRendered = true
            }
        }
        player.addListener(listener)
        onDispose {
            player.removeListener(listener)
            ReelPlayerManager.releasePlayer(player)
        }
    }

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            key = { reels[it].id }
        ) { page ->
            val reel = reels[page]
            ReelPageItem(
                reel = reel,
                isCurrentPage = (page == activePageIndex),
                hasFirstFrame = hasFirstFrameRendered,
                playerView = playerView,
                player = player,
                onNextClick = {
                    if (page < reels.size - 1) {
                        scope.launch { pagerState.animateScrollToPage(page + 1) }
                    }
                },
                onPrevClick = {
                    if (page > 0) {
                        scope.launch { pagerState.animateScrollToPage(page - 1) }
                    }
                },
                onCommentClick = { showCommentsSheet = true },
                onShareClick = { showShareSheet = true }
            )
        }

        // Top Navigation Header with Back Arrow Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 44.dp, start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    when {
                        showCommentsSheet -> showCommentsSheet = false
                        showShareSheet -> showShareSheet = false
                        else -> onBackClick?.invoke()
                    }
                },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.4f))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = "Reels",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // 💬 Comments Bottom Sheet
        if (showCommentsSheet) {
            CommentsBottomSheet(
                onDismiss = { showCommentsSheet = false }
            )
        }

        // 📤 Share Bottom Sheet
        if (showShareSheet) {
            ShareBottomSheet(
                reelTitle = reels.getOrNull(activePageIndex)?.title ?: "Check out this reel!",
                onDismiss = { showShareSheet = false }
            )
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun ReelPageItem(
    reel: ReelItem,
    isCurrentPage: Boolean,
    hasFirstFrame: Boolean,
    playerView: PlayerView,
    player: ExoPlayer,
    onNextClick: () -> Unit,
    onPrevClick: () -> Unit,
    onCommentClick: () -> Unit,
    onShareClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var isLiked by remember { mutableStateOf(reel.isLiked) }
    var showThumbsUpAnim by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(player.isPlaying) }
    var showControlsOverlay by remember { mutableStateOf(false) }

    // Synchronize isPlaying state directly with ExoPlayer listener
    DisposableEffect(player, isCurrentPage) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                if (isCurrentPage) {
                    isPlaying = playing
                }
            }
        }
        player.addListener(listener)
        if (isCurrentPage) {
            isPlaying = player.isPlaying
        }
        onDispose {
            player.removeListener(listener)
        }
    }

    val thumbnailAlpha by animateFloatAsState(
        targetValue = if (reel.isAiImage) 1f else if (isCurrentPage && hasFirstFrame) 0f else 1f,
        label = "thumbnailAlpha"
    )

    // Automatically hide ThumbsUp animation after 800ms
    LaunchedEffect(showThumbsUpAnim) {
        if (showThumbsUpAnim) {
            delay(800)
            showThumbsUpAnim = false
        }
    }

    // ⏱️ Auto-dismiss control overlay after 2 seconds when playing
    LaunchedEffect(showControlsOverlay, isPlaying) {
        if (showControlsOverlay && isPlaying) {
            delay(2000)
            showControlsOverlay = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(reel.isAiImage) {
                if (!reel.isAiImage) {
                    detectTapGestures(
                        onTap = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            showControlsOverlay = true
                            if (player.isPlaying) {
                                player.pause()
                                isPlaying = false
                            } else {
                                player.play()
                                isPlaying = true
                            }
                        },
                        onDoubleTap = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            // Double tap: Like reel with ThumbsUp animation!
                            isLiked = true
                            showThumbsUpAnim = true
                        }
                    )
                }
            }
    ) {
        // Video Player (Only attached for normal video reels)
        if (isCurrentPage && !reel.isAiImage) {
            AndroidView(
                factory = { ctx ->
                    FrameLayout(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                },
                update = { container ->
                    (playerView.parent as? ViewGroup)?.removeView(playerView)
                    if (playerView.parent !== container) {
                        container.addView(
                            playerView,
                            FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Image Display (Always visible for AI Artwork, or crossfaded thumbnail for Video Reels)
        AsyncImage(
            model = reel.thumbnailUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(thumbnailAlpha)
        )

        // Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.4f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.75f)
                        )
                    )
                )
        )

        // 🎬 YouTube-Style Centered Vertical Controls Stack (Only shown for video reels)
        AnimatedVisibility(
            visible = !reel.isAiImage && (showControlsOverlay || !isPlaying),
            enter = fadeIn(spring()),
            exit = fadeOut(spring()),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // 1. Previous Reel Button (Above Play/Pause)
                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onPrevClick()
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.55f))
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowUp,
                        contentDescription = "Previous Reel Above",
                        tint = Color.White,
                        modifier = Modifier.size(34.dp)
                    )
                }

                // 2. Play / Pause Button (Exact Center with Haptic Feedback)
                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        if (player.isPlaying) {
                            player.pause()
                            isPlaying = false
                        } else {
                            player.play()
                            isPlaying = true
                            showControlsOverlay = false
                        }
                    },
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.65f))
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = BrandEmerald,
                        modifier = Modifier.size(48.dp)
                    )
                }

                // 3. Next Reel Button (Below Play/Pause)
                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onNextClick()
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.55f))
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Next Reel Below",
                        tint = Color.White,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }
        }

        // 👍 Thumbs Up Double Tap Animation (Auto-dismisses after 800ms via key-triggered LaunchedEffect)
        AnimatedVisibility(
            visible = showThumbsUpAnim,
            enter = scaleIn(spring(stiffness = 300f)) + fadeIn(),
            exit = scaleOut() + fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Icon(
                imageVector = Icons.Filled.ThumbUp,
                contentDescription = null,
                tint = BrandEmerald,
                modifier = Modifier.size(100.dp)
            )
        }

        // Right-Side Social Actions (Like 👍, Comment 💬, Share 📤) - Only for Video Reels
        AnimatedVisibility(
            visible = !reel.isAiImage,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 48.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // 👍 Thumbs Up Like Button
                IconButton(onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    isLiked = !isLiked
                }) {
                    Icon(
                        imageVector = if (isLiked) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                        contentDescription = "Like",
                        tint = if (isLiked) BrandEmerald else Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Text(
                    text = reel.likesCount,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                IconButton(onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onCommentClick()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Comment,
                        contentDescription = "Comment",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
                Text(
                    text = reel.commentsCount,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                IconButton(onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onShareClick()
                }) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = "Share",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }

        // Bottom Creator Profile Info (User's Photo: karthik_avatar)
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 48.dp, end = 80.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = reel.userAvatar),
                    contentDescription = "User Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(MintSurface)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "@${reel.username}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = reel.title,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp,
                maxLines = 2
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.MusicNote,
                    contentDescription = null,
                    tint = BrandEmerald,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Original Audio - @${reel.username}",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

// ── Dummy Comments Bottom Sheet ────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CommentsBottomSheet(onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var commentText by remember { mutableStateOf("") }
    val comments = remember {
        mutableStateListOf(
            CommentItem("alex_runner", "Insane video! Love the edit 🔥", "2h", "142"),
            CommentItem("sports_fanatic", "What camera did you use for this?", "4h", "89"),
            CommentItem("badminton_ace", "Super smooth transitions! 🏸👏", "5h", "45"),
            CommentItem("court_king", "Need a tutorial for that move! 🏀", "1d", "210"),
            CommentItem("bmx_rider", "Top tier content as always 🚲💯", "2d", "67")
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        scrimColor = Color.Black.copy(alpha = 0.6f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Comments (${comments.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(comments) { comment ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.karthik_avatar),
                            contentDescription = "Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "@${comment.username}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = comment.timeAgo,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = comment.text,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Write a comment input bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp, top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    placeholder = { Text("Add a comment...", fontSize = 13.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandEmerald,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (commentText.isNotBlank()) {
                            comments.add(0, CommentItem("karthik", commentText, "Just now", "0"))
                            commentText = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(BrandEmerald)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

// ── Dummy Share Sheet ─────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShareBottomSheet(reelTitle: String, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()

    val shareApps = listOf(
        Pair("WhatsApp", "💬"),
        Pair("Instagram", "📸"),
        Pair("Twitter / X", "🌐"),
        Pair("Copy Link", "🔗"),
        Pair("Messages", "✉️"),
        Pair("More Options", "📤")
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        scrimColor = Color.Black.copy(alpha = 0.6f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "Share Reel to",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                shareApps.take(4).forEach { (name, icon) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                putExtra(Intent.EXTRA_TEXT, "Check out this reel: $reelTitle")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Reel"))
                            onDismiss()
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(icon, fontSize = 24.sp)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(name, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
