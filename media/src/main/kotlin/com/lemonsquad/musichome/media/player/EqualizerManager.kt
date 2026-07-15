package com.lemonsquad.musichome.media.player

import android.media.audiofx.Equalizer
import android.util.Log
import com.lemonsquad.musichome.core.domain.model.EqualizerSettings

class EqualizerManager {
    private var equalizer: Equalizer? = null
    private var currentSettings = EqualizerSettings()

    fun attachToSession(audioSessionId: Int) {
        try {
            if (audioSessionId == 0) return
            
            release()
            equalizer = Equalizer(0, audioSessionId)
            applySettings(currentSettings)
        } catch (e: Exception) {
            Log.e("EqualizerManager", "Failed to attach Equalizer to session $audioSessionId", e)
        }
    }

    fun release() {
        equalizer?.enabled = false
        equalizer?.release()
        equalizer = null
    }

    fun setBandLevel(band: Short, level: Short) {
        try {
            equalizer?.setBandLevel(band, level)
            // Update internal settings state
            val newBands = currentSettings.bands.toMutableMap()
            newBands[band.toInt()] = level.toInt()
            currentSettings = currentSettings.copy(bands = newBands)
        } catch (e: Exception) {
            Log.e("EqualizerManager", "Failed to set band level", e)
        }
    }

    fun setEnabled(enabled: Boolean) {
        try {
            equalizer?.enabled = enabled
            currentSettings = currentSettings.copy(enabled = enabled)
        } catch (e: Exception) {
            Log.e("EqualizerManager", "Failed to set enabled state", e)
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
        currentSettings = settings
        equalizer?.let { eq ->
            eq.enabled = settings.enabled
            settings.bands.forEach { (band, level) ->
                try {
                    eq.setBandLevel(band.toShort(), level.toShort())
                } catch (e: Exception) {
                    Log.e("EqualizerManager", "Failed to apply band $band", e)
                }
            }
        }
    }
}
