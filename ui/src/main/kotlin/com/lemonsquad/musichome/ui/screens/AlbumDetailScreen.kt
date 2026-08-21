package com.lemonsquad.musichome.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.lemonsquad.musichome.core.domain.model.Song
import com.lemonsquad.musichome.ui.theme.MetallicGray
import com.lemonsquad.musichome.ui.theme.PureBlack
import com.lemonsquad.musichome.ui.theme.WalkmanOrange
import com.lemonsquad.musichome.ui.viewmodels.AlbumDetailUiState
import com.lemonsquad.musichome.ui.viewmodels.MusicViewModel

@Composable
fun AlbumDetailScreen(
    viewModel: MusicViewModel,
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.albumDetailState.collectAsState()

    when (val state = uiState) {
        is AlbumDetailUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = WalkmanOrange)
            }
        }
        is AlbumDetailUiState.Success -> {
            AlbumDetailContent(state, viewModel, onBack)
        }
        is AlbumDetailUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error loading album details", color = Color.White)
            }
        }
    }
}

@Composable
fun AlbumDetailContent(
    state: AlbumDetailUiState.Success, 
    viewModel: MusicViewModel,
    onBack: () -> Unit = {}
) {
    val dominantColor = state.dominantColor?.let { Color(it) } ?: PureBlack
    
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            dominantColor.copy(alpha = 0.5f),
            PureBlack
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        // Back Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    com.lemonsquad.musichome.ui.icons.MusicHomeIcons.Back, 
                    contentDescription = "Back", 
                    tint = WalkmanOrange
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "ALBUM", 
                color = MetallicGray, 
                fontSize = 12.sp, 
                fontWeight = FontWeight.Bold, 
                letterSpacing = 2.sp
            )
        }

        // Header Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = state.artworkUri,
                contentDescription = null,
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.DarkGray),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(24.dp))
            
            Column {
                Text(
                    text = state.album.title,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 28.sp
                )
                Text(
                    text = state.album.artist.uppercase(),
                    color = WalkmanOrange,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 2.sp
                )
                state.album.year?.let {
                    Text(text = it.toString(), color = MetallicGray, fontSize = 14.sp)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { viewModel.playAlbum(state.album, state.songs) },
                        colors = ButtonDefaults.buttonColors(containerColor = WalkmanOrange),
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("PLAY ALL", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    OutlinedButton(
                        onClick = { viewModel.shuffleAlbum(state.album, state.songs) },
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(brush = Brush.linearGradient(listOf(Color.White, Color.Transparent))),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Shuffle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SHUFFLE", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Tracklist
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            itemsIndexed(state.songs) { index, song ->
                AlbumSongItem(song = song, onClick = { viewModel.playAlbum(state.album, state.songs, index) })
            }
        }
    }
}

@Composable
fun AlbumSongItem(song: Song, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = song.trackNumber.toString().padStart(2, '0'),
                color = MetallicGray,
                fontSize = 14.sp,
                modifier = Modifier.width(32.dp)
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(text = song.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Text(text = song.artist, color = MetallicGray, fontSize = 12.sp)
            }
            
            Text(
                text = formatDuration(song.duration),
                color = MetallicGray,
                fontSize = 12.sp
            )
        }
    }
}
