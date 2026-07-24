package com.lemonsquad.musichome.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.lemonsquad.musichome.ui.theme.WalkmanOrange
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun VUMeter(
    magnitude: Float,
    modifier: Modifier = Modifier,
    color: Color = WalkmanOrange
) {
    // Smoothed magnitude
    val smoothedMagnitude = remember { Animatable(0f) }
    LaunchedEffect(magnitude) {
        smoothedMagnitude.animateTo(
            targetValue = magnitude,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        )
    }

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2, size.height * 0.9f)
        val radius = size.width * 0.4f
        
        // Gauge Arc
        drawArc(
            color = Color.DarkGray,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )
        
        // Needle
        val angle = 180f + (smoothedMagnitude.value * 180f)
        val angleRad = Math.toRadians(angle.toDouble())
        val needleLen = radius * 0.9f
        val needleEnd = Offset(
            (center.x + needleLen * cos(angleRad)).toFloat(),
            (center.y + needleLen * sin(angleRad)).toFloat()
        )
        
        drawLine(
            color = color,
            start = center,
            end = needleEnd,
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
        
        // Hub
        drawCircle(
            color = Color.Black,
            radius = 8.dp.toPx(),
            center = center
        )
        drawCircle(
            color = color,
            radius = 4.dp.toPx(),
            center = center,
            style = Stroke(width = 1.dp.toPx())
        )
    }
}
