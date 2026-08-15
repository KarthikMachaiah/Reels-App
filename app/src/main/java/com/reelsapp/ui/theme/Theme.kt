package com.reelsapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary          = BrandEmerald,
    onPrimary        = SoftWhite,
    primaryContainer = MintSurface,
    onPrimaryContainer = TextPrimary,
    secondary        = BrandEmeraldLight,
    onSecondary      = TextPrimary,
    secondaryContainer = Color(0xFFD6F5E8),
    onSecondaryContainer = TextPrimary,
    background       = OffWhite,
    onBackground     = TextPrimary,
    surface          = SoftWhite,
    onSurface        = TextPrimary,
    surfaceVariant   = MintSurface,
    onSurfaceVariant = TextSecondary,
    error            = ErrorRed,
    outline          = BrandEmeraldLight
)

private val DarkColorScheme = darkColorScheme(
    primary          = BrandEmeraldLight,
    onPrimary        = DeepBlack,
    primaryContainer = BrandEmeraldDark,
    onPrimaryContainer = MintSurface,
    secondary        = MintSurface,
    onSecondary      = DeepBlack,
    secondaryContainer = MintSurfaceDark,
    onSecondaryContainer = MintSurface,
    background       = DeepBlack,
    onBackground     = TextOnDarkBg,
    surface          = DarkSurface,
    onSurface        = TextOnDarkBg,
    surfaceVariant   = MintSurfaceDark,
    onSurfaceVariant = TextSecondaryDark,
    error            = ErrorRed,
    outline          = BrandEmeraldDark
)

@Composable
fun ReelsAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else      -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = ReelsTypography,
        content     = content
    )
}
