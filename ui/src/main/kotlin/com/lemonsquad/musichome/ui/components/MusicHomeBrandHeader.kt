package com.lemonsquad.musichome.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lemonsquad.musichome.ui.theme.AudioState
import com.lemonsquad.musichome.ui.theme.MusicHomeTypography
import com.lemonsquad.musichome.ui.theme.WalkmanOrange

@Composable
fun MusicHomeBrandHeader(
    audioState: AudioState,
    modifier: Modifier = Modifier,
    isWifiConnected: Boolean = false,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "Pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "LedPulse"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Hardware-style LED Indicator
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .alpha(if (audioState == AudioState.SCANNING) pulseAlpha else 1f)
                    .background(
                        color = audioState.ledColor,
                        shape = CircleShape
                    )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "MUSIC HOME",
                fontFamily = MusicHomeTypography.BrandingFont,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp,
                color = Color.White
            )

            if (isWifiConnected) {
                Spacer(modifier = Modifier.width(12.dp))
                // WiFi indicator (subtle ◉)
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                        .padding(1.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize().background(Color.White, CircleShape))
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp)) 
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Premium Accent Line
        Box(
            modifier = Modifier
                .width(28.dp)
                .height(2.dp)
                .background(WalkmanOrange)
        )
    }
}
