package com.lemonsquad.musichome.media.player

import android.media.audiofx.Visualizer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.hypot

class VisualizerManager {
    private var visualizer: Visualizer? = null
    
    private val _spectrum = MutableStateFlow(FloatArray(16) { 0f })
    val spectrum = _spectrum.asStateFlow()

    fun initialize(audioSessionId: Int) {
        try {
            if (audioSessionId == 0) return
            
            visualizer = Visualizer(audioSessionId).apply {
                captureSize = Visualizer.getCaptureSizeRange()[1]
                setDataCaptureListener(object : Visualizer.OnDataCaptureListener {
                    override fun onWaveFormDataCapture(v: Visualizer?, waveform: ByteArray?, samplingRate: Int) {}

                    override fun onFftDataCapture(v: Visualizer?, fft: ByteArray?, samplingRate: Int) {
                        fft?.let { processFft(it) }
                    }
                }, Visualizer.getMaxCaptureRate() / 2, false, true)
                enabled = true
            }
        } catch (e: Exception) {
            Log.e("VisualizerManager", "Failed to initialize Visualizer", e)
        }
    }

    private fun processFft(fft: ByteArray) {
        val bands = FloatArray(16)
        val numBins = fft.size / 2
        
        // Simple mapping of FFT bins to 16 bars (logarithmic-ish)
        for (i in 0 until 16) {
            val startBin = (i * numBins / 16).coerceAtLeast(1)
            val endBin = ((i + 1) * numBins / 16).coerceAtMost(numBins - 1)
            
            var magnitudeSum = 0f
            for (j in startBin until endBin) {
                val real = fft[j * 2].toFloat()
                val imag = fft[j * 2 + 1].toFloat()
                magnitudeSum += hypot(real, imag)
            }
            
            val avgMagnitude = if (endBin > startBin) magnitudeSum / (endBin - startBin) else 0f
            // Normalize to roughly 0.0 - 1.0 range (Visualizer FFT values are signed 8-bit)
            bands[i] = (avgMagnitude / 64f).coerceIn(0f, 1f)
        }
        _spectrum.value = bands
    }

    fun release() {
        visualizer?.enabled = false
        visualizer?.release()
        visualizer = null
    }
}
