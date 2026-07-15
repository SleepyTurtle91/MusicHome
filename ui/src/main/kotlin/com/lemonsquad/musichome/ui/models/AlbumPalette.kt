package com.lemonsquad.musichome.ui.models

import androidx.compose.ui.graphics.Color

data class AlbumPalette(
    val dominant: Color = Color.Black,
    val darkVibrant: Color = Color.DarkGray,
    val muted: Color = Color.Gray,
    val lightVibrant: Color = Color.LightGray
)
