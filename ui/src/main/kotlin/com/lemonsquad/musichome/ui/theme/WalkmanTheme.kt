package com.lemonsquad.musichome.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val WalkmanOrange = Color(0xFFFF6A00)
val PureBlack = Color(0xFF000000)
val NearBlack = Color(0xFF080808)
val DarkSurface = Color(0xFF151515)
val MetallicGray = Color(0xFF999999)
val GoldAccent = Color(0xFFFFD700)

object MusicHomeTypography {
    val BrandingFont = FontFamily.SansSerif
}

private val DarkColorScheme = darkColorScheme(
    primary = WalkmanOrange,
    secondary = MetallicGray,
    background = NearBlack,
    surface = DarkSurface,
    onBackground = Color.White,
    onSurface = Color.White,
)

@Composable
fun WalkmanTheme(
    content: @Composable () -> Unit
) {
    androidx.compose.material3.MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography(),
        content = content
    )
}
