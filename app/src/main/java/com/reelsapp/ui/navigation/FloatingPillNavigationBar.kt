package com.reelsapp.ui.navigation

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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

    Column(
        modifier = modifier
            .padding(horizontal = 18.dp, vertical = 12.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // High-Fidelity Translucent Glass Card Container (Dribbble 26294545)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 32.dp,
                    shape = RoundedCornerShape(40.dp),
                    ambientColor = Color.Black.copy(alpha = 0.5f),
                    spotColor = Color.Black.copy(alpha = 0.6f)
                )
                .clip(RoundedCornerShape(40.dp))
                // Translucent Liquid Glass Surface
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF3B3D45).copy(alpha = 0.45f),
                            Color(0xFF1E2026).copy(alpha = 0.75f)
                        )
                    )
                )
                // Specular Light Rim
                .border(
                    width = 1.5.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.65f),
                            Color.White.copy(alpha = 0.15f)
                        )
                    ),
                    shape = RoundedCornerShape(40.dp)
                )
                .padding(top = 10.dp, bottom = 12.dp, start = 12.dp, end = 12.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Top "Capture" Frosted Glass Pill
                Box(
                    modifier = Modifier
                        .height(30.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.22f),
                                    Color.White.copy(alpha = 0.08f)
                                )
                            )
                        )
                        .border(
                            width = 1.dp,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.70f),
                                    Color.White.copy(alpha = 0.20f)
                                )
                            ),
                            shape = RoundedCornerShape(15.dp)
                        )
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.CameraAlt,
                            contentDescription = "Capture",
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Capture",
                            color = Color.White,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Inner Liquid Glass Dock Layout
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.12f),
                                    Color.White.copy(alpha = 0.04f)
                                )
                            )
                        )
                        .border(
                            width = 1.dp,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.40f),
                                    Color.White.copy(alpha = 0.10f)
                                )
                            ),
                            shape = RoundedCornerShape(32.dp)
                        )
                        .padding(4.dp),
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
                                targetValue = if (isSelected) 1.04f else 1.0f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium
                                ),
                                label = "tabScale"
                            )

                            // 3D Glass Selected Dome (original-2cf5fc24ef7c55b847ab0578bc63b790.webp)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .scale(scale)
                                    .clip(RoundedCornerShape(28.dp))
                                    .then(
                                        if (isSelected) {
                                            Modifier
                                                .background(
                                                    Brush.verticalGradient(
                                                        colors = listOf(
                                                            Color.White.copy(alpha = 0.38f),
                                                            Color.White.copy(alpha = 0.12f)
                                                        )
                                                    )
                                                )
                                                .border(
                                                    width = 1.25.dp,
                                                    brush = Brush.verticalGradient(
                                                        colors = listOf(
                                                            Color.White.copy(alpha = 0.95f),
                                                            Color.White.copy(alpha = 0.30f)
                                                        )
                                                    ),
                                                    shape = RoundedCornerShape(28.dp)
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
                                        tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.70f),
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        text = item.title,
                                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.70f),
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
        }
    }
}
