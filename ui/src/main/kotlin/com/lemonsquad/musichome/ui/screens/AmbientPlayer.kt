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
import com.lemonsquad.musichome.ui.viewmodels.MusicViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AmbientPlayer(
    viewModel: MusicViewModel,
    onExit: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val spectrum by viewModel.spectrum.collectAsState()
    val playbackStatus by viewModel.playbackStatus.collectAsState()
    val palette by viewModel.currentPalette.collectAsState()

    var currentTime by remember { mutableStateOf(SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())) }
    
    val dominantAnimate by animateColorAsState(palette.dominant, animationSpec = tween(2000))

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            delay(10000)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
            .clickable { onExit() }
    ) {
        // Subtle background glow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(dominantAnimate.copy(alpha = 0.15f), Color.Transparent),
                        center = androidx.compose.ui.geometry.Offset(x = 500f, y = 500f) // Top left-ish
                    )
                )
        )

        val successState = uiState as? com.lemonsquad.musichome.ui.viewmodels.MusicUiState.Success
        val currentSong = successState?.songs?.find { it.id.toString() == playbackStatus.currentSongId }

        Column(
            modifier = Modifier.fillMaxSize(),
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

            Spacer(modifier = Modifier.height(48.dp))

            if (currentSong != null) {
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
                .alpha(0.5f)
        )
    }
}

val DpVectorConverter = TwoWayConverter<androidx.compose.ui.unit.Dp, AnimationVector1D>(
    { AnimationVector1D(it.value) },
    { it.value.dp }
)
