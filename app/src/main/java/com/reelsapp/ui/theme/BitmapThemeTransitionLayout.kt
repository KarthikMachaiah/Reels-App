package com.reelsapp.ui.theme

import android.graphics.Bitmap
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import kotlinx.coroutines.launch

@Composable
fun BitmapThemeTransitionLayout(
    isDarkTheme: Boolean,
    content: @Composable (isDark: Boolean, onToggle: () -> Unit) -> Unit
) {
    var currentDarkState by remember { mutableStateOf(isDarkTheme) }
    var previousBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    val transitionProgress = remember { Animatable(1f) }
    val graphicsLayer = rememberGraphicsLayer()
    val scope = rememberCoroutineScope()

    fun toggleTheme() {
        if (transitionProgress.isRunning) return
        scope.launch {
            try {
                val snapshot = graphicsLayer.toImageBitmap()
                val androidBitmap = snapshot.asAndroidBitmap()
                previousBitmap = androidBitmap.copy(Bitmap.Config.ARGB_8888, true).asImageBitmap()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            currentDarkState = !currentDarkState
        }
    }

    LaunchedEffect(currentDarkState) {
        if (previousBitmap != null) {
            transitionProgress.snapTo(0f)
            transitionProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 600)
            )
            previousBitmap = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent {
                    graphicsLayer.record {
                        this@drawWithContent.drawContent()
                    }
                    drawLayer(graphicsLayer)
                }
        ) {
            content(currentDarkState) { toggleTheme() }
        }

        previousBitmap?.let { oldBitmap ->
            Image(
                bitmap = oldBitmap,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = 1f - transitionProgress.value
                        scaleX = 1f + (transitionProgress.value * 0.05f)
                        scaleY = 1f + (transitionProgress.value * 0.05f)
                    }
            )
        }
    }
}
