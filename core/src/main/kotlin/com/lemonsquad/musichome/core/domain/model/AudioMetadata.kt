package com.lemonsquad.musichome.core.domain.model

data class AudioMetadata(
    val sampleRate: Int? = null,
    val bitDepth: Int? = null,
    val format: String? = null,
    val replayGain: Float? = null,
    val albumGain: Float? = null,
    val peak: Float? = null,
    val loudnessRange: Float? = null // DR - Dynamic Range
)
