package com.lemonsquad.musichome.core.domain.model

data class EqualizerSettings(
    val enabled: Boolean = false,
    val bands: Map<Int, Int> = emptyMap(), // Frequency (Hz) -> Gain (mB)
    val presetName: String? = null
)
