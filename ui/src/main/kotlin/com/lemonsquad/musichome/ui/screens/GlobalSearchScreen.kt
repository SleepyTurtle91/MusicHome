package com.lemonsquad.musichome.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lemonsquad.musichome.ui.theme.MetallicGray
import com.lemonsquad.musichome.ui.theme.PureBlack
import com.lemonsquad.musichome.ui.theme.WalkmanOrange
import com.lemonsquad.musichome.ui.viewmodels.MusicViewModel
import com.lemonsquad.musichome.ui.viewmodels.MusicUiState
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalSearchScreen(
    viewModel: MusicViewModel,
    onBack: () -> Unit,
    onSongClick: (com.lemonsquad.musichome.core.domain.model.Song) -> Unit = {},
    onAlbumClick: (com.lemonsquad.musichome.core.domain.model.Album) -> Unit = {},
    onArtistClick: (com.lemonsquad.musichome.core.domain.model.Artist) -> Unit = {}
) {
    var query by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    
    val filteredResults = remember(query, uiState) {
        if (query.length < 2 || uiState !is MusicUiState.Success) {
            emptyMap<String, List<Any>>()
        } else {
            val state = uiState as MusicUiState.Success
            val q = query.lowercase()
            
            mapOf(
                "SONGS" to state.songs.filter { it.title.lowercase().contains(q) || it.artist.lowercase().contains(q) },
                "ALBUMS" to state.albums.filter { it.title.lowercase().contains(q) || it.artist.lowercase().contains(q) },
                "ARTISTS" to state.artists.filter { it.name.lowercase().contains(q) }
            ).filterValues { it.isNotEmpty() }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
            .padding(16.dp)
    ) {
        // Search Header
        TextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0A0A0A), RoundedCornerShape(8.dp)),
            placeholder = { Text("Search library...", color = MetallicGray) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = WalkmanOrange) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { query = "" }) {
                        Icon(Icons.Default.Close, null, tint = Color.White)
                    }
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                cursorColor = WalkmanOrange,
                focusedIndicatorColor = WalkmanOrange,
                unfocusedIndicatorColor = Color.DarkGray
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (query.length >= 2) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                filteredResults.forEach { (category, items) ->
                    item {
                        Text(
                            text = category,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = WalkmanOrange,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        items.forEach { item ->
                            SearchResultItem(
                                item = item,
                                onClick = {
                                    when (item) {
                                        is com.lemonsquad.musichome.core.domain.model.Song -> onSongClick(item)
                                        is com.lemonsquad.musichome.core.domain.model.Album -> onAlbumClick(item)
                                        is com.lemonsquad.musichome.core.domain.model.Artist -> onArtistClick(item)
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
                
                if (filteredResults.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(top = 64.dp), contentAlignment = Alignment.Center) {
                            Text("No results for \"$query\"", color = MetallicGray)
                        }
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Search, null, modifier = Modifier.size(64.dp), tint = Color.DarkGray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Type to start searching", color = MetallicGray)
                }
            }
        }
    }
}

@Composable
fun SearchResultItem(item: Any, onClick: () -> Unit = {}) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF151515),
        shape = RoundedCornerShape(4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Minimalist icon/thumb placeholder
            Box(modifier = Modifier.size(40.dp).background(Color.DarkGray, RoundedCornerShape(4.dp)))
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                val (title, subtitle) = when (item) {
                    is com.lemonsquad.musichome.core.domain.model.Song -> item.title to item.artist
                    is com.lemonsquad.musichome.core.domain.model.Album -> item.title to item.artist
                    is com.lemonsquad.musichome.core.domain.model.Artist -> item.name to "${item.albumCount} Albums"
                    else -> "Unknown" to "Unknown"
                }
                
                Text(text = title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(text = subtitle, color = MetallicGray, fontSize = 12.sp, maxLines = 1)
            }
        }
    }
}
