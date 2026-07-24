package com.lemonsquad.musichome.ui.models

data class AudioPipelineState(
    val source: String = "Unknown",
    val processing: List<String> = emptyList(),
    val engine: String = "Media3",
    val output: String = "Internal DAC",
    val verification: VerificationStatus = VerificationStatus.UNKNOWN,
    val isOffloadActive: Boolean = false
)
