package com.lemonsquad.musichome.ui.viewmodels

data class PlaybackStatus(
    val position: Long = 0L,
    val duration: Long = 0L,
    val isPlaying: Boolean = false,
    val currentSongId: String? = null
) {
    val progress: Float
        get() = if (duration > 0) position.toFloat() / duration else 0f
}
