package com.lemonsquad.musichome.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.lemonsquad.musichome.core.domain.model.Album
import com.lemonsquad.musichome.core.domain.model.Artist
import com.lemonsquad.musichome.core.domain.model.Song
import com.lemonsquad.musichome.ui.components.FolderBrowser
import com.lemonsquad.musichome.ui.icons.MusicHomeIcons
import com.lemonsquad.musichome.ui.theme.DarkSurface
import com.lemonsquad.musichome.ui.theme.MetallicGray
import com.lemonsquad.musichome.ui.theme.PureBlack
import com.lemonsquad.musichome.ui.theme.WalkmanOrange
import com.lemonsquad.musichome.ui.viewmodels.MusicUiState
import com.lemonsquad.musichome.ui.viewmodels.MusicViewModel

@Composable
fun LibraryScreen(
    viewModel: MusicViewModel,
    onAlbumClick: (Album) -> Unit = {},
    onEditMetadata: (Song) -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Songs", "Albums", "Artists")
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
    ) {
        // Hardware-style Contextual Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            tabs.forEachIndexed { index, title ->
                val isSelected = selectedTab == index
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isSelected) WalkmanOrange else Color(0xFF151515))
                        .clickable { selectedTab = index }
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = title.uppercase(),
                        color = if (isSelected) Color.Black else Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        when (val state = uiState) {
            is MusicUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = WalkmanOrange)
                }
            }
            is MusicUiState.Empty -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(
                            "♪",
                            fontSize = 48.sp,
                            color = WalkmanOrange.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "LIBRARY EMPTY",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "No music storage detected. Configure your music locations in Settings to begin.",
                            color = MetallicGray,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = onNavigateToSettings,
                            colors = ButtonDefaults.buttonColors(containerColor = WalkmanOrange),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text("CONFIGURE STORAGE", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        }
                    }
                }
            }
            is MusicUiState.Success -> {
                when (selectedTab) {
                    0 -> SongList(state.songs, viewModel, isTablet, onEditMetadata)
                    1 -> AlbumGrid(state.albums, viewModel, isTablet, onAlbumClick)
                    2 -> ArtistList(state.artists, viewModel, isTablet)
                    else -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("TAB COMING SOON", color = MetallicGray)
                    }
                }
            }
        }
    }
}

@Composable
fun SongList(songs: List<Song>, viewModel: MusicViewModel, isTablet: Boolean, onEditMetadata: (Song) -> Unit) {
    if (isTablet) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 300.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(songs) { song ->
                SongListItem(song = song, onClick = { viewModel.playSong(song, songs) }, onEditMetadata = { onEditMetadata(song) })
            }
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(songs) { song ->
                SongListItem(song = song, onClick = { viewModel.playSong(song, songs) }, onEditMetadata = { onEditMetadata(song) })
            }
        }
    }
}

@Composable
fun AlbumGrid(
    albums: List<Album>,
    viewModel: MusicViewModel,
    isTablet: Boolean,
    onAlbumClick: (Album) -> Unit
) {
    val columns = if (isTablet) GridCells.Adaptive(minSize = 200.dp) else GridCells.Fixed(2)
    
    LazyVerticalGrid(
        columns = columns,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        items(albums) { album ->
            AlbumGridItem(album = album, onClick = { onAlbumClick(album) })
        }
    }
}

@Composable
fun ArtistList(artists: List<Artist>, viewModel: MusicViewModel, isTablet: Boolean) {
    if (isTablet) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 250.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(artists) { artist ->
                ArtistListItem(artist = artist, onClick = { /* TODO: Open Artist Details */ })
            }
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(artists) { artist ->
                ArtistListItem(artist = artist, onClick = { /* TODO: Open Artist Details */ })
            }
        }
    }
}

@Composable
fun AlbumGridItem(album: Album, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = album.artworkUri,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF1A1A1A)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = album.title,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = album.artist,
            color = MetallicGray,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "${album.songCount} SONGS",
            color = WalkmanOrange.copy(alpha = 0.8f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun ArtistListItem(artist: Artist, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = artist.artworkUri,
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(32.dp)) // Circular for artists
                .background(Color(0xFF1A1A1A)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(20.dp))
        Column {
            Text(
                text = artist.name,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                text = "${artist.albumCount} ALBUMS • ${artist.songCount} SONGS",
                color = MetallicGray,
                fontSize = 12.sp,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun SongGridItem(song: Song, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            AsyncImage(
                model = song.artwork,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = song.title, color = Color.White, fontSize = 16.sp, maxLines = 1)
                Text(text = song.artist, color = MetallicGray, fontSize = 14.sp, maxLines = 1)
            }
        }
    }
}

@Composable
fun SongListItem(song: Song, onClick: () -> Unit, onEditMetadata: () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = song.artwork,
            contentDescription = null,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.DarkGray),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = song.title, color = Color.White, fontSize = 16.sp, maxLines = 1)
            Text(text = song.artist, color = MetallicGray, fontSize = 14.sp, maxLines = 1)
        }
        
        Box {
            IconButton(onClick = { showMenu = true }) {
                Icon(MusicHomeIcons.More, contentDescription = "More", tint = MetallicGray)
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                modifier = Modifier.background(DarkSurface)
            ) {
                DropdownMenuItem(
                    text = { Text("EDIT METADATA", color = Color.White, fontSize = 12.sp) },
                    onClick = {
                        showMenu = false
                        onEditMetadata()
                    }
                )
            }
        }
    }
}
