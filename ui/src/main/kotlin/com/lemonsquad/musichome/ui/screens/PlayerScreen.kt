package com.lemonsquad.musichome.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.lemonsquad.musichome.ui.theme.MetallicGray
import com.lemonsquad.musichome.ui.theme.PureBlack
import com.lemonsquad.musichome.ui.theme.WalkmanOrange
import com.lemonsquad.musichome.ui.viewmodels.MusicUiState
import com.lemonsquad.musichome.ui.viewmodels.MusicViewModel
import kotlin.math.abs

@Composable
fun PlayerScreen(viewModel: MusicViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
            .padding(16.dp)
    ) {
        when (val state = uiState) {
            is MusicUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is MusicUiState.Empty -> {
                Text(
                    "No Music Found",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is MusicUiState.Success -> {
                val currentSong = state.songs.firstOrNull() // Simplified for now
                if (currentSong != null) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Album Art (Top 60%)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.6f)
                                .clip(RoundedCornerShape(24.dp))
                                .background(Color.DarkGray)
                                .pointerInput(Unit) {
                                    detectDragGestures(
                                        onDragEnd = { /* Gesture logic here */ },
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            if (abs(dragAmount.x) > 50) {
                                                if (dragAmount.x > 0) viewModel.skipToPrevious()
                                                else viewModel.skipToNext()
                                            }
                                        }
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = currentSong.artwork,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            
                            if (currentSong.artwork == null) {
                                Icon(
                                    Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(100.dp),
                                    tint = Color.Gray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Track Info
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = currentSong.title,
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = currentSong.artist,
                                    color = MetallicGray,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .border(1.dp, Color(0xFFFFD700), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        "MP3",
                                        color = Color(0xFFFFD700),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Progress Bar
                        LinearProgressIndicator(
                            progress = { 0.3f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp),
                            color = WalkmanOrange,
                            trackColor = Color.DarkGray,
                            strokeCap = androidx.compose.ui.graphics.StrokeCap.Butt
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Transport Controls
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { viewModel.skipToPrevious() }) {
                                Icon(Icons.Default.FastRewind, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.White)
                            }
                            
                            LargeFloatingActionButton(
                                onClick = { viewModel.pause() },
                                containerColor = WalkmanOrange,
                                contentColor = Color.White,
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(Icons.Default.Pause, contentDescription = null, modifier = Modifier.size(48.dp))
                            }

                            IconButton(onClick = { viewModel.skipToNext() }) {
                                Icon(Icons.Default.FastForward, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.White)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}
