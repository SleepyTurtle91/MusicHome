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
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
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
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
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
                    if (isTablet) {
                        TabletPlayerLayout(currentSong, viewModel)
                    } else {
                        PhonePlayerLayout(currentSong, viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun TabletPlayerLayout(currentSong: com.lemonsquad.musichome.core.domain.model.Song, viewModel: MusicViewModel) {
    Row(modifier = Modifier.fillMaxSize()) {
        // Left: Massive Artwork
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1.2f)
                .background(Color.DarkGray)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = { },
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
                Icon(Icons.Default.PlayArrow, null, Modifier.size(200.dp), Color.Gray)
            }
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
                text = currentSong.title,
                color = Color.White,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 48.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = currentSong.artist,
                    color = MetallicGray,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(16.dp))
                Box(
                    modifier = Modifier
                        .border(1.dp, Color(0xFFFFD700), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("FLAC", color = Color(0xFFFFD700), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(64.dp))

            // Linear Appliance Progress Bar
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("0:45", color = Color.Gray, fontSize = 14.sp)
                    Text("4:20", color = Color.Gray, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { 0.18f },
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                    color = WalkmanOrange,
                    trackColor = Color(0xFF1A1A1A)
                )
            }

            Spacer(modifier = Modifier.height(64.dp))

            // Large Transport Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.skipToPrevious() }, modifier = Modifier.size(64.dp)) {
                    Icon(Icons.Default.FastRewind, null, Modifier.size(48.dp), Color.White)
                }
                
                LargeFloatingActionButton(
                    onClick = { viewModel.pause() },
                    containerColor = WalkmanOrange,
                    contentColor = Color.White,
                    modifier = Modifier.size(88.dp)
                ) {
                    Icon(Icons.Default.Pause, null, Modifier.size(48.dp))
                }

                IconButton(onClick = { viewModel.skipToNext() }, modifier = Modifier.size(64.dp)) {
                    Icon(Icons.Default.FastForward, null, Modifier.size(48.dp), Color.White)
                }
            }
        }
    }
}

@Composable
fun PhonePlayerLayout(currentSong: com.lemonsquad.musichome.core.domain.model.Song, viewModel: MusicViewModel) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.DarkGray),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(currentSong.artwork, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = currentSong.title, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text(text = currentSong.artist, color = MetallicGray, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.weight(1f))

        LinearProgressIndicator(
            progress = { 0.3f },
            modifier = Modifier.fillMaxWidth().height(4.dp),
            color = WalkmanOrange,
            trackColor = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(48.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.skipToPrevious() }) {
                Icon(Icons.Default.FastRewind, null, Modifier.size(48.dp), Color.White)
            }
            LargeFloatingActionButton(onClick = { viewModel.pause() }, containerColor = WalkmanOrange) {
                Icon(Icons.Default.Pause, null, Modifier.size(48.dp))
            }
            IconButton(onClick = { viewModel.skipToNext() }) {
                Icon(Icons.Default.FastForward, null, Modifier.size(48.dp), Color.White)
            }
        }
        Spacer(modifier = Modifier.height(48.dp))
    }
}
