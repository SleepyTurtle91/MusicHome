package com.lemonsquad.musichome.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.lemonsquad.musichome.core.domain.model.Song
import com.lemonsquad.musichome.ui.icons.MusicHomeIcons
import com.lemonsquad.musichome.ui.theme.*
import com.lemonsquad.musichome.ui.viewmodels.MusicUiState

@Composable
fun DapHomeScreen(
    uiState: MusicUiState,
    onNavigateToDestination: (String) -> Unit,
    onSongClick: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        is MusicUiState.Loading -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(DapZincBg),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = WalkmanOrange)
            }
        }
        is MusicUiState.Error -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(DapZincBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error: ${uiState.message}",
                    color = Color.Red,
                    fontSize = 14.sp
                )
            }
        }
        is MusicUiState.Empty -> {
            DapHomeContent(
                recentSongs = emptyList(),
                onNavigateToDestination = onNavigateToDestination,
                onSongClick = onSongClick,
                modifier = modifier
            )
        }
        is MusicUiState.Success -> {
            DapHomeContent(
                recentSongs = uiState.songs.take(3),
                onNavigateToDestination = onNavigateToDestination,
                onSongClick = onSongClick,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun DapHomeContent(
    recentSongs: List<Song>,
    onNavigateToDestination: (String) -> Unit,
    onSongClick: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DapZincBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        // 2x2 Primary Hardware Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DapGridTile(
                        title = "Library",
                        icon = MusicHomeIcons.Library,
                        accentColor = WalkmanOrange,
                        onClick = { onNavigateToDestination(MusicDestination.Library.route) },
                        modifier = Modifier.weight(1f)
                    )
                    DapGridTile(
                        title = "Storage",
                        icon = MusicHomeIcons.Folder,
                        accentColor = Color.White,
                        onClick = { onNavigateToDestination(MusicDestination.ROUTE_SETTINGS_LIBRARY) },
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DapGridTile(
                        title = "Playlists",
                        icon = MusicHomeIcons.Queue,
                        accentColor = Color.White,
                        onClick = { onNavigateToDestination(MusicDestination.ROUTE_QUEUE) },
                        modifier = Modifier.weight(1f)
                    )
                    DapGridTile(
                        title = "Audio Engine",
                        icon = MusicHomeIcons.Sound,
                        accentColor = WalkmanOrange,
                        onClick = { onNavigateToDestination(MusicDestination.Sound.route) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Recently Added Section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RECENTLY ADDED",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MetallicGray,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "VIEW ALL",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = WalkmanOrange,
                        letterSpacing = 1.sp,
                        modifier = Modifier.clickable { onNavigateToDestination(MusicDestination.Library.route) }
                    )
                }

                if (recentSongs.isEmpty()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        color = DapPanel,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DapBorder)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "No recent tracks found",
                                fontSize = 12.sp,
                                color = MetallicGray
                            )
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        recentSongs.forEach { song ->
                            DapRecentTrackCard(
                                song = song,
                                onClick = { onSongClick(song) }
                            )
                        }
                    }
                }
            }
        }

        // System Storage Breakdown Footer
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = DapPanel,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DapBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "STORAGE MODE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MetallicGray,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "LOCAL MEDIA STORAGE ACTIVE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun DapGridTile(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(110.dp)
            .clickable { onClick() },
        color = DapPanel,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DapBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(PureBlack)
                    .border(1.dp, DapBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun DapRecentTrackCard(
    song: Song,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = DapPanel,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DapBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = song.artwork,
                contentDescription = song.title,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(PureBlack),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = song.artist,
                    fontSize = 12.sp,
                    color = MetallicGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (song.mimeType.isNotEmpty() && song.mimeType != "audio/*") {
                Surface(
                    color = WalkmanOrange.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(4.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, WalkmanOrange.copy(alpha = 0.3f))
                ) {
                    val displayMime = if (song.mimeType.contains("/")) {
                        song.mimeType.substringAfter("/")
                    } else {
                        song.mimeType
                    }.uppercase().take(8)
                    Text(
                        text = displayMime,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = WalkmanOrange,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
