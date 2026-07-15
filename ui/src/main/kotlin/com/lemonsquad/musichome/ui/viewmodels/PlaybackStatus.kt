package com.lemonsquad.musichome.ui.viewmodels

data class PlaybackStatus(
    val position: Long = 0L,
    val duration: Long = 0L,
    val isPlaying: Boolean = false,
    val currentSongId: String? = null,
    // High-honesty metadata
    val bitrate: Int? = null,
    val sampleRate: Int? = null,
    val bitDepth: Int? = null,
    val format: String? = null,
    val channels: Int? = null
) {
    val progress: Float
        get() = if (duration > 0) position.toFloat() / duration else 0f

    val isHighRes: Boolean
        get() = (bitDepth ?: 0) >= 24 || (sampleRate ?: 0) >= 88200

    val isLossless: Boolean
        get() = format?.lowercase()?.let { 
            it.contains("flac") || it.contains("wav") || it.contains("alac") || it.contains("dsd") 
        } ?: false
}
