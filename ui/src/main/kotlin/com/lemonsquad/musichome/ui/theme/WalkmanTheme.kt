package com.lemonsquad.musichome.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

val WalkmanOrange = Color(0xFFFF6A00)
val PureBlack = Color(0xFF000000)
val NearBlack = Color(0xFF080808)
val DarkSurface = Color(0xFF151515)
val MetallicGray = Color(0xFF999999)
val GoldAccent = Color(0xFFFFD700)

// DAP Surface Depth Tokens (Zinc Appliance Scheme)
val DapZincBg = Color(0xFF09090B)
val DapPanel = Color(0xFF18181B)
val DapBorder = Color(0xFF27272A)

object MusicHomeTypography {
    val BrandingFont = FontFamily.SansSerif
}

val LocalAccentColor = staticCompositionLocalOf { WalkmanOrange }
val LocalTrueBlack = staticCompositionLocalOf { true }

@Composable
fun WalkmanTheme(
    accentColor: Color = WalkmanOrange,
    trueBlack: Boolean = true,
    content: @Composable () -> Unit
) {
    val bg = if (trueBlack) PureBlack else DapZincBg
    val surface = if (trueBlack) NearBlack else DapPanel
    val border = if (trueBlack) Color(0xFF151515) else DapBorder

    val colorScheme = darkColorScheme(
        primary = accentColor,
        secondary = MetallicGray,
        background = bg,
        surface = surface,
        onBackground = Color.White,
        onSurface = Color.White,
        surfaceVariant = border
    )

    CompositionLocalProvider(
        LocalAccentColor provides accentColor,
        LocalTrueBlack provides trueBlack
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography(),
            content = content
        )
    }
}
