package com.reelsapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reelsapp.R
import com.reelsapp.reels.ComposeReelsFeed
import com.reelsapp.reels.ReelItem
import com.reelsapp.ui.theme.BrandEmerald
import com.reelsapp.ui.theme.BrandEmeraldLight
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AiReelsScreen(
    onReelsPlaybackStateChanged: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var prompt by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }
    var generatedReels by remember { mutableStateOf<List<ReelItem>?>(null) }
    val scope = rememberCoroutineScope()

    // Pulse animation for AI badge
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    if (generatedReels != null) {
        Box(modifier = Modifier.fillMaxSize()) {
            ComposeReelsFeed(
                reels = generatedReels!!,
                onBackClick = {
                    generatedReels = null
                    onReelsPlaybackStateChanged?.invoke(false)
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    } else {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .padding(top = 28.dp, bottom = 90.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Pulsing AI Icon Badge
                Box(
                    modifier = Modifier
                        .scale(pulseScale)
                        .size(84.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    BrandEmerald.copy(alpha = 0.25f),
                                    BrandEmeraldLight.copy(alpha = 0.08f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = "AI Reels",
                        tint = BrandEmerald,
                        modifier = Modifier.size(46.dp)
                    )
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    text = "AI Reel Generator",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "Enter any script or topic. Gemini AI will automatically script, edit, and render a dedicated vertical AI Reel feed for you!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(top = 8.dp, bottom = 28.dp)
                )

                // Input Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Describe your AI Reel",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = prompt,
                            onValueChange = { prompt = it },
                            placeholder = { Text("e.g. Cyberpunk City 2030, Deep Ocean Secrets, or Extreme Mountain Biking...") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 5,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrandEmerald,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                        )

                        Spacer(Modifier.height(20.dp))

                        Button(
                            onClick = {
                                if (prompt.isNotBlank() && !isGenerating) {
                                    isGenerating = true
                                    scope.launch {
                                        delay(1500)
                                        val cleanPrompt = prompt.trim()
                                        generatedReels = listOf(
                                            ReelItem(
                                                id = "ai_1_${System.currentTimeMillis()}",
                                                videoUrl = "android.resource://com.reelsapp.debug/${R.raw.reel_1}",
                                                thumbnailUrl = "https://images.unsplash.com/photo-1519766304817-4f37bda74a29?q=80&w=600",
                                                title = "🤖 AI Reel: $cleanPrompt 🌊 #GeminiAI #Generated",
                                                username = "gemini_ai_bot",
                                                userAvatar = R.drawable.karthik_avatar,
                                                likesCount = "98.4K",
                                                commentsCount = "4.2K"
                                            ),
                                            ReelItem(
                                                id = "ai_2_${System.currentTimeMillis()}",
                                                videoUrl = "android.resource://com.reelsapp.debug/${R.raw.reel_2}",
                                                thumbnailUrl = "https://images.unsplash.com/photo-1534438327276-14e5300c3a48?q=80&w=600",
                                                title = "⚡ AI Scene 2: $cleanPrompt in motion 🚲",
                                                username = "gemini_ai_bot",
                                                userAvatar = R.drawable.karthik_avatar,
                                                likesCount = "124.1K",
                                                commentsCount = "5.8K"
                                            ),
                                            ReelItem(
                                                id = "ai_3_${System.currentTimeMillis()}",
                                                videoUrl = "android.resource://com.reelsapp.debug/${R.raw.reel_3}",
                                                thumbnailUrl = "https://images.unsplash.com/photo-1546519638-68e109498ffc?q=80&w=600",
                                                title = "✨ AI Scene 3: $cleanPrompt final cut 🛒",
                                                username = "gemini_ai_bot",
                                                userAvatar = R.drawable.karthik_avatar,
                                                likesCount = "88.9K",
                                                commentsCount = "3.1K"
                                            )
                                        )
                                        isGenerating = false
                                        onReelsPlaybackStateChanged?.invoke(true)
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(26.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandEmerald,
                                contentColor = Color.White
                            ),
                            enabled = prompt.isNotBlank() && !isGenerating
                        ) {
                            if (isGenerating) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.5.dp
                                )
                                Spacer(Modifier.width(10.dp))
                                Text("Gemini AI is Generating...", fontWeight = FontWeight.Bold)
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("Generate AI Reel Feed", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(28.dp))

                // Quick Prompt Suggestions
                Text(
                    text = "Try Popular AI Prompts",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )

                val suggestions = listOf(
                    "🌊 Deep sea bioluminescent ocean exploration",
                    "🚴 Extreme mountain biking downhill action",
                    "🛒 Future AI smart supermarket 2030 vlog",
                    "⚡ High speed cyber kinetic neon visuals"
                )

                suggestions.forEach { suggestion ->
                    Card(
                        onClick = { prompt = suggestion },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = null,
                                tint = BrandEmerald,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = suggestion,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}
