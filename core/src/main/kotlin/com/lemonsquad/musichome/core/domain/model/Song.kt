package com.lemonsquad.musichome.core.domain.model

import android.net.Uri

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String?,
    val duration: Long,
    val artwork: Uri?,
    val mediaUri: Uri,
    val path: String,
    val trackNumber: Int = 0,
    val replayGain: Float? = null,
    val albumGain: Float? = null,
    val replayPeak: Float? = null,
    val loudnessRange: Float? = null // DR Value
)
