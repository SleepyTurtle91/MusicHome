package com.lemonsquad.musichome.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.lemonsquad.musichome.ui.theme.MetallicGray
import com.lemonsquad.musichome.ui.theme.WalkmanOrange
import com.lemonsquad.musichome.ui.viewmodels.MusicUiState
import com.lemonsquad.musichome.ui.viewmodels.MusicViewModel

@Composable
fun MiniPlayer(
    viewModel: MusicViewModel,
    onClick: () -> Unit
) {
    val playbackStatus by viewModel.playbackStatus.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    // Find the current song from the uiState based on playbackStatus
    val currentSong = (uiState as? MusicUiState.Success)?.songs?.find { 
        it.id.toString() == playbackStatus.currentSongId 
    }

    if (currentSong != null) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clickable { onClick() },
            color = Color(0xFF121212),
            tonalElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = currentSong.artwork,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.DarkGray),
                    contentScale = ContentScale.Crop
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = currentSong.title,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Text(
                        text = currentSong.artist,
                        color = MetallicGray,
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.skipToPrevious() }) {
                        Icon(
                            Icons.Default.SkipPrevious,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    IconButton(onClick = { 
                        if (playbackStatus.isPlaying) viewModel.pause() else viewModel.resume() 
                    }) {
                        Icon(
                            if (playbackStatus.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = WalkmanOrange
                        )
                    }

                    IconButton(onClick = { viewModel.skipToNext() }) {
                        Icon(
                            Icons.Default.SkipNext,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}
