package com.lemonsquad.musichome.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.lemonsquad.musichome.ui.components.SpectrumVisualizer
import com.lemonsquad.musichome.ui.theme.PureBlack
import com.lemonsquad.musichome.ui.theme.WalkmanOrange
import com.lemonsquad.musichome.ui.models.DeviceState
import com.lemonsquad.musichome.ui.viewmodels.MusicUiState
import com.lemonsquad.musichome.ui.viewmodels.MusicViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AmbientPlayer(
    viewModel: MusicViewModel,
    onExit: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState(initial = MusicUiState.Loading)
    val spectrum by viewModel.spectrum.collectAsState(initial = FloatArray(16))
    val deviceState by viewModel.deviceState.collectAsState()
    val palette by viewModel.currentPalette.collectAsState()

    var currentTime by remember { mutableStateOf(SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())) }
    var isDimmed by remember { mutableStateOf(false) }

    // Pixel Shifting logic for OLED protection
    val shiftTransition = rememberInfiniteTransition(label = "PixelShift")
    val shiftX by shiftTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(60000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ShiftX"
    )
    val shiftY by shiftTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(45000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ShiftY"
    )

    val dominantAnimate by animateColorAsState(palette.dominant, animationSpec = tween(2000), label = "Glow")
    val ambientAlpha by animateFloatAsState(if (isDimmed) 0.3f else 1f, animationSpec = tween(2000), label = "Dim")

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            // Dim after 2 minutes in Ambient mode
            delay(120000)
            isDimmed = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
            .clickable { 
                if (isDimmed) isDimmed = false else onExit() 
            }
            .alpha(ambientAlpha)
    ) {
        // Subtle background glow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = shiftX.dp, y = shiftY.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(dominantAnimate.copy(alpha = 0.15f), Color.Transparent),
                        center = androidx.compose.ui.geometry.Offset(x = 500f, y = 500f)
                    )
                )
        )

        val successState = uiState as? com.lemonsquad.musichome.ui.viewmodels.MusicUiState.Success
        val currentSong = successState?.songs?.find { it.id.toString() == deviceState.playback.currentSongId }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = (shiftX / 2).dp, y = (shiftY / 2).dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Clock
            Text(
                text = currentTime,
                color = Color.White,
                fontSize = 120.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = 8.sp,
                modifier = Modifier.alpha(0.8f)
            )

            if (currentSong != null) {
                Spacer(modifier = Modifier.height(48.dp))
                
                // Floating Artwork
                val infiniteTransition = rememberInfiniteTransition(label = "ArtworkFloat")
                val floatOffset by infiniteTransition.animateValue(
                    initialValue = 0.dp,
                    targetValue = 10.dp,
                    typeConverter = DpVectorConverter,
                    animationSpec = infiniteRepeatable(
                        animation = tween(4000, easing = EaseInOutSine),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "Float"
                )

                Box(modifier = Modifier.offset(y = floatOffset)) {
                    AsyncImage(
                        model = currentSong.artwork,
                        contentDescription = null,
                        modifier = Modifier
                            .size(240.dp)
                            .alpha(0.9f),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = currentSong.title,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = currentSong.artist,
                    color = WalkmanOrange,
                    fontSize = 18.sp,
                    maxLines = 1
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // DAP Metrics
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${deviceState.playback.format ?: "PCM"} ${deviceState.playback.sampleRate?.let { "${it/1000}kHz" } ?: ""}",
                        color = WalkmanOrange,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = if (deviceState.network.isWifiConnected) "WIFI ●" else "OFFLINE ○",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "BAT ${deviceState.power.batteryPercent}%",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 10.sp
                    )
                }
            }
        }

        // Slow pulsing visualizer at the bottom
        SpectrumVisualizer(
            spectrum = spectrum,
            color = palette.darkVibrant,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp, start = 64.dp, end = 48.dp)
                .alpha(0.5f),
            fpsLimit = deviceState.settings.visualizerFps
        )
    }
}

val DpVectorConverter = TwoWayConverter<androidx.compose.ui.unit.Dp, AnimationVector1D>(
    { AnimationVector1D(it.value) },
    { it.value.dp }
)
