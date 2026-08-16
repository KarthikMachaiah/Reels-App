package com.reelsapp.ui.navigation

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reelsapp.ui.theme.BrandEmerald

data class TabItem(
    val tab: AppTab,
    val title: String,
    val icon: ImageVector
)

val navTabs = listOf(
    TabItem(AppTab.HOME, "Home", Icons.Filled.Home),
    TabItem(AppTab.AI_IMAGE, "AI", Icons.Filled.AutoAwesome),
    TabItem(AppTab.JUST_REELS, "Reels", Icons.Filled.PlayArrow),
    TabItem(AppTab.PROFILE, "Profile", Icons.Filled.Person)
)

@Composable
fun FloatingPillNavigationBar(
    currentTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = modifier
            .padding(horizontal = 24.dp, vertical = 18.dp)
            .fillMaxWidth()
            .height(70.dp)
            .shadow(
                elevation = 20.dp,
                shape = CircleShape,
                ambientColor = Color.Black.copy(alpha = 0.15f),
                spotColor = Color.Black.copy(alpha = 0.25f)
            )
            .clip(CircleShape)
            // 1:1 Hardware RenderEffect Pipeline: Chained 40f Blur + 1.6x ColorMatrix Saturation Boost
            .graphicsLayer {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val blurEffect = RenderEffect.createBlurEffect(
                        40f, 40f,
                        Shader.TileMode.CLAMP
                    )
                    val matrix = ColorMatrix().apply { setSaturation(1.6f) }
                    val colorFilterEffect = RenderEffect.createColorFilterEffect(
                        ColorMatrixColorFilter(matrix)
                    )
                    renderEffect = RenderEffect.createChainEffect(blurEffect, colorFilterEffect).asComposeRenderEffect()
                }
            }
            // 1:1 Base Tint: Crisp white layer with exact 0.28f alpha blend
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.72f),
                        Color.White.copy(alpha = 0.58f)
                    )
                )
            )
            // Crisp Specular Glass Rim Highlight Edge
            .border(
                width = 1.25.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.95f),
                        Color.White.copy(alpha = 0.40f)
                    )
                ),
                shape = CircleShape
            )
            .padding(5.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navTabs.forEach { item ->
                val isSelected = currentTab == item.tab
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.05f else 1.0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "tabScale"
                )

                // Dark Selection Pill Capsule Highlight with Green Brand Accent (BrandEmerald)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .scale(scale)
                        .clip(CircleShape)
                        .then(
                            if (isSelected) {
                                Modifier.background(
                                    Color.Black.copy(alpha = 0.12f)
                                )
                            } else Modifier
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onTabSelected(item.tab)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = if (isSelected) BrandEmerald else Color(0xFF636366),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = item.title,
                            color = if (isSelected) BrandEmerald else Color(0xFF636366),
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }
        }
    }
}
