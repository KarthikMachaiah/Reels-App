package com.reelsapp.ui.components.glass

import android.os.Build
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.hazeChild

@Composable
fun GlassTopAppBar(
    title: String,
    scrollFraction: Float,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
) {
    val fraction = scrollFraction.coerceIn(0f, 1f)

    val cornerRadius by animateDpAsState(
        targetValue = lerp(0.dp, 26.dp, fraction),
        animationSpec = tween(200),
        label = "glassCorner",
    )
    val barHeight by animateDpAsState(
        targetValue = lerp(96.dp, 60.dp, fraction),
        animationSpec = tween(200),
        label = "glassHeight",
    )
    val horizontalInset by animateDpAsState(
        targetValue = lerp(0.dp, 12.dp, fraction),
        animationSpec = tween(200),
        label = "glassInset",
    )

    val supportsShader = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    val lightPosition = if (supportsShader) rememberGlassLightPosition() else null
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalInset, vertical = if (fraction > 0f) 8.dp else 0.dp)
            .height(barHeight)
            .clip(RoundedCornerShape(cornerRadius))
            // Backdrop frosted glass blur via Haze
            .hazeChild(
                state = hazeState,
                style = HazeStyle(
                    tint = dev.chrisbanes.haze.HazeTint(Color.White.copy(alpha = 0.14f)),
                    blurRadius = 22.dp,
                    noiseFactor = 0.06f,
                ),
            )
            .let { base ->
                if (supportsShader && lightPosition != null) {
                    base.liquidGlassRefraction(
                        cornerRadiusPx = { with(density) { cornerRadius.toPx() } },
                        lightPosition = lightPosition,
                    )
                } else {
                    // Pre-API 33 fallback: static gradient edge + highlight
                    base.glassEdge(cornerRadius).specularHighlight(cornerRadius)
                }
            },
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.SemiBold,
            fontSize = lerp(28.sp, 18.sp, fraction),
            color = Color.Black.copy(alpha = 0.92f),
            modifier = Modifier.padding(start = 20.dp),
        )
    }
}

private fun Modifier.glassEdge(cornerRadius: Dp): Modifier = this.border(
    width = 1.dp,
    brush = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.55f),
            Color.White.copy(alpha = 0.05f),
            Color.White.copy(alpha = 0.25f),
        ),
        start = Offset(0f, 0f),
        end = Offset(1000f, 1000f),
    ),
    shape = RoundedCornerShape(cornerRadius),
)

private fun Modifier.specularHighlight(cornerRadius: Dp): Modifier = this.background(
    brush = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.20f),
            Color.Transparent,
            Color.White.copy(alpha = 0.08f),
        ),
        start = Offset(0f, 0f),
        end = Offset(600f, 400f),
    ),
    shape = RoundedCornerShape(cornerRadius),
)
