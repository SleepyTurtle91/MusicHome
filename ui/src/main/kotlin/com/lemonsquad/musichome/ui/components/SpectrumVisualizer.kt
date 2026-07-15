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
    color: Color = WalkmanOrange
) {
    // Smoothed spectrum values to avoid flickering
    val smoothedSpectrum = remember { FloatArray(16) { 0f } }
    
    // Using a derived state to ensure we only update when spectrum changes
    // and to apply basic smoothing
    val displayedSpectrum = remember(spectrum) {
        for (i in spectrum.indices) {
            // Very basic smoothing: 80% current, 20% target
            smoothedSpectrum[i] = smoothedSpectrum[i] * 0.2f + spectrum[i] * 0.8f
        }
        smoothedSpectrum.copyOf()
    }

    Canvas(modifier = modifier) {
        val barCount = displayedSpectrum.size
        val spacing = 4.dp.toPx()
        val totalSpacing = spacing * (barCount - 1)
        val barWidth = (size.width - totalSpacing) / barCount
        
        val gradient = Brush.verticalGradient(
            colors = listOf(
                color,
                color.copy(alpha = 0.3f)
            )
        )

        for (i in displayedSpectrum.indices) {
            val magnitude = displayedSpectrum[i]
            val barHeight = size.height * magnitude
            
            drawRect(
                brush = gradient,
                topLeft = Offset(
                    x = i * (barWidth + spacing),
                    y = size.height - barHeight
                ),
                size = Size(barWidth, barHeight)
            )
        }
    }
}
