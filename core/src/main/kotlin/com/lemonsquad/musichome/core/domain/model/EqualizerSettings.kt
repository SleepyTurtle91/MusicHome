package com.lemonsquad.musichome.core.domain.model

data class EqualizerSettings(
    val enabled: Boolean = false,
    val bands: Map<Int, Int> = emptyMap(), // Frequency Index -> Gain (mB)
    val presetName: String? = null
) {
    companion object {
        val PRESETS = mapOf(
            "FLAT" to listOf(0, 0, 0, 0, 0),
            "ROCK" to listOf(400, 200, -100, 200, 500),
            "POP" to listOf(-200, 100, 400, 100, -200),
            "JAZZ" to listOf(300, 100, -200, 200, 300),
            "BASS BOOST" to listOf(600, 300, 0, 0, 0)
        )
    }
}
