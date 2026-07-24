package com.lemonsquad.musichome.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lemonsquad.musichome.ui.theme.WalkmanOrange

@Composable
fun SpectrumVisualizer(
    spectrum: FloatArray,
    modifier: Modifier = Modifier,
    color: Color = WalkmanOrange,
    fpsLimit: Int = 30
) {
    // Smoothed spectrum values to avoid flickering
    val smoothedSpectrum = remember { FloatArray(16) { 0f } }
    
    // FPS Throttling logic
    var lastFrameTime by remember { mutableLongStateOf(0L) }
    val frameDuration = 1000L / fpsLimit

    val displayedSpectrum = remember(spectrum) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastFrameTime >= frameDuration) {
            lastFrameTime = currentTime
            for (i in spectrum.indices) {
                smoothedSpectrum[i] = smoothedSpectrum[i] * 0.3f + spectrum[i] * 0.7f
            }
        }
        smoothedSpectrum.copyOf()
    }

    Canvas(modifier = modifier) {
        val barCount = displayedSpectrum.size
        val spacing = 2.dp.toPx() // Thinner spacing
        val totalSpacing = spacing * (barCount - 1)
        val barWidth = (size.width - totalSpacing) / barCount
        
        val segmentHeight = 2.dp.toPx()
        val segmentSpacing = 1.dp.toPx()

        for (i in displayedSpectrum.indices) {
            val magnitude = displayedSpectrum[i].coerceIn(0f, 1f)
            val barHeight = size.height * magnitude
            
            // Draw segmented bars for "Hardware" feel
            var currentY = size.height
            while (currentY > size.height - barHeight) {
                val alpha = (currentY / size.height).coerceIn(0.3f, 1f)
                drawRect(
                    color = color.copy(alpha = alpha),
                    topLeft = Offset(
                        x = i * (barWidth + spacing),
                        y = currentY - segmentHeight
                    ),
                    size = Size(barWidth, segmentHeight)
                )
                currentY -= (segmentHeight + segmentSpacing)
            }
        }
    }
}
