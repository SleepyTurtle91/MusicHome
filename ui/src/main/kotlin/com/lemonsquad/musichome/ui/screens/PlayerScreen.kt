package com.lemonsquad.musichome.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.lemonsquad.musichome.core.domain.model.Song
import com.lemonsquad.musichome.ui.components.SpectrumVisualizer
import com.lemonsquad.musichome.ui.theme.MetallicGray
import com.lemonsquad.musichome.ui.theme.PureBlack
import com.lemonsquad.musichome.ui.theme.WalkmanOrange
import com.lemonsquad.musichome.ui.viewmodels.MusicUiState
import com.lemonsquad.musichome.ui.viewmodels.MusicViewModel
import com.lemonsquad.musichome.ui.viewmodels.PlaybackStatus

@Composable
fun PlayerScreen(viewModel: MusicViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val spectrum by viewModel.spectrum.collectAsState()
    val playbackStatus by viewModel.playbackStatus.collectAsState()
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val isTablet = configuration.screenWidthDp >= 600
    
    var isArtworkFocusMode by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
    ) {
        when (val state = uiState) {
            is MusicUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = WalkmanOrange)
            }
            is MusicUiState.Empty -> {
                Text(
                    "NO MEDIA DETECTED",
                    color = MetallicGray,
                    letterSpacing = 2.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is MusicUiState.Success -> {
                // Find the song currently playing or default to the first one
                val currentSong = state.songs.find { it.id.toString() == playbackStatus.currentSongId } ?: state.songs.firstOrNull()
                
                if (currentSong != null) {
                    if (isArtworkFocusMode) {
                        ArtworkFocusLayout(currentSong, spectrum, playbackStatus, onExit = { isArtworkFocusMode = false })
                    } else {
                        if (isTablet && !isPortrait) {
                            TabletPlayerLayout(currentSong, spectrum, playbackStatus, viewModel, onFocusRequest = { isArtworkFocusMode = true })
                        } else {
                            PhonePlayerLayout(currentSong, spectrum, playbackStatus, viewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TabletPlayerLayout(
    currentSong: com.lemonsquad.musichome.core.domain.model.Song,
    spectrum: FloatArray,
    status: com.lemonsquad.musichome.ui.viewmodels.PlaybackStatus,
    viewModel: MusicViewModel,
    onFocusRequest: () -> Unit
) {
    Row(modifier = Modifier.fillMaxSize()) {
        // Left: Massive Artwork with Focus trigger
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1.2f)
                .background(Color.Black)
                .clickable { onFocusRequest() },
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = currentSong.artwork,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            
            // Visualizer overlaying bottom of artwork
            SpectrumVisualizer(
                spectrum = spectrum,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(horizontal = 48.dp, vertical = 24.dp)
                    .align(Alignment.BottomCenter)
                    .alpha(0.6f)
            )
        }

        // Right: Hardware Control Panel
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .padding(48.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = currentSong.title.uppercase(),
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                lineHeight = 40.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = currentSong.artist,
                    color = WalkmanOrange,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(16.dp))
                Box(
                    modifier = Modifier
                        .border(1.dp, Color(0xFFFFD700), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("FLAC / 24-BIT", color = Color(0xFFFFD700), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(64.dp))

            // Linear Appliance Progress Bar
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(formatDuration(status.position), color = MetallicGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(formatDuration(status.duration), color = MetallicGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { status.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = WalkmanOrange,
                    trackColor = Color(0xFF1A1A1A)
                )
            }

            Spacer(modifier = Modifier.height(64.dp))

            // Large Transport Controls (Machined style)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.skipToPrevious() }, modifier = Modifier.size(72.dp)) {
                    Icon(Icons.Default.SkipPrevious, null, Modifier.size(40.dp), Color.White)
                }
                
                Surface(
                    onClick = { if (status.isPlaying) viewModel.pause() else viewModel.resume() },
                    shape = RoundedCornerShape(44.dp),
                    color = WalkmanOrange,
                    modifier = Modifier.size(88.dp),
                    shadowElevation = 8.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            if (status.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, 
                            null, 
                            Modifier.size(48.dp), 
                            Color.White
                        )
                    }
                }

                IconButton(onClick = { viewModel.skipToNext() }, modifier = Modifier.size(72.dp)) {
                    Icon(Icons.Default.SkipNext, null, Modifier.size(40.dp), Color.White)
                }
            }
        }
    }
}

@Composable
fun PhonePlayerLayout(
    currentSong: Song,
    spectrum: FloatArray,
    status: PlaybackStatus,
    viewModel: MusicViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
    ) {
        // --- TOP 40%: ARTWORK DISPLAY ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.4f)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = currentSong.artwork,
                contentDescription = null,
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        }

        // --- BOTTOM 60%: HARDWARE INTERFACE ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f)
                .padding(horizontal = 32.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Info Area
            Text(
                text = currentSong.title.uppercase(),
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                maxLines = 2,
                textAlign = TextAlign.Center,
                lineHeight = 32.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = currentSong.artist,
                color = WalkmanOrange,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Visualizer ("Screen Feedback")
            SpectrumVisualizer(
                spectrum = spectrum,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .alpha(0.8f)
            )

            Spacer(modifier = Modifier.weight(1f))

            // 3. Progress Section
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(formatDuration(status.position), color = MetallicGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(formatDuration(status.duration), color = MetallicGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { status.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = WalkmanOrange,
                    trackColor = Color(0xFF1A1A1A)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // 4. Hardware-style Transport Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HardwareButton(
                    icon = Icons.Default.SkipPrevious,
                    onClick = { viewModel.skipToPrevious() }
                )
                
                HardwareButton(
                    icon = if (status.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    onClick = { if (status.isPlaying) viewModel.pause() else viewModel.resume() },
                    isPrimary = true
                )

                HardwareButton(
                    icon = Icons.Default.SkipNext,
                    onClick = { viewModel.skipToNext() }
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun HardwareButton(
    icon: ImageVector,
    onClick: () -> Unit,
    isPrimary: Boolean = false
) {
    val size = if (isPrimary) 88.dp else 72.dp
    val iconSize = if (isPrimary) 44.dp else 36.dp
    
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = if (isPrimary) WalkmanOrange else Color(0xFF1A1A1A),
        modifier = Modifier.size(size),
        shadowElevation = 8.dp,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(iconSize),
                tint = Color.White
            )
        }
    }
}

@Composable
fun ArtworkFocusLayout(
    currentSong: com.lemonsquad.musichome.core.domain.model.Song,
    spectrum: FloatArray,
    status: com.lemonsquad.musichome.ui.viewmodels.PlaybackStatus,
    onExit: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
            .clickable { onExit() }
    ) {
        AsyncImage(
            model = currentSong.artwork,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.6f
        )
        
        // Gradient overlay for readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, PureBlack.copy(alpha = 0.8f))
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = currentSong.title.uppercase(),
                color = Color.White,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp,
                maxLines = 1
            )
            Text(
                text = currentSong.artist,
                color = WalkmanOrange,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 2.sp
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            SpectrumVisualizer(
                spectrum = spectrum,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .alpha(0.9f)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "${formatDuration(status.position)} / ${formatDuration(status.duration)}",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

fun formatDuration(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
