package com.lemonsquad.musichome.media.player

import android.media.audiofx.Equalizer
import android.util.Log
import com.lemonsquad.musichome.core.domain.model.EqualizerSettings

class EqualizerManager {
    private var equalizer: Equalizer? = null

    fun initialize(audioSessionId: Int) {
        try {
            equalizer = Equalizer(0, audioSessionId)
            equalizer?.enabled = true
        } catch (e: Exception) {
            Log.e("EqualizerManager", "Failed to initialize Equalizer", e)
        }
    }

    fun release() {
        equalizer?.release()
        equalizer = null
    }

    fun setBandLevel(band: Short, level: Short) {
        try {
            equalizer?.setBandLevel(band, level)
        } catch (e: Exception) {
            Log.e("EqualizerManager", "Failed to set band level", e)
        }
    }

    fun getBandLevel(band: Short): Short {
        return try {
            equalizer?.getBandLevel(band) ?: 0
        } catch (e: Exception) {
            0
        }
    }

    fun getBandCount(): Short = equalizer?.numberOfBands ?: 0
    
    fun getCenterFreq(band: Short): Int = equalizer?.getCenterFreq(band) ?: 0
    
    fun getBandLevelRange(): ShortArray = equalizer?.bandLevelRange ?: shortArrayOf(0, 0)

    fun applySettings(settings: EqualizerSettings) {
        equalizer?.enabled = settings.enabled
        // Mapping bands would happen here if we used a more complex domain model
    }
}
