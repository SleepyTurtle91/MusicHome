package com.lemonsquad.musichome.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lemonsquad.musichome.organizer.data.SongEntity
import com.lemonsquad.musichome.organizer.ui.LibraryToolsViewModel
import com.lemonsquad.musichome.ui.theme.WalkmanOrange
import com.lemonsquad.musichome.ui.theme.NearBlack
import com.lemonsquad.musichome.ui.theme.MetallicGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetadataEditorScreen(
    song: SongEntity,
    viewModel: LibraryToolsViewModel,
    onSaved: () -> Unit
) {
    var title by remember { mutableStateOf(song.title) }
    var artist by remember { mutableStateOf(song.artist) }
    var album by remember { mutableStateOf(song.album) }
    var genre by remember { mutableStateOf(song.genre ?: "") }
    var year by remember { mutableStateOf(song.year?.toString() ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("EDIT METADATA", style = MaterialTheme.typography.titleMedium, letterSpacing = 2.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NearBlack),
                actions = {
                    IconButton(onClick = {
                        viewModel.updateSong(song.copy(
                            title = title,
                            artist = artist,
                            album = album,
                            genre = genre.ifBlank { null },
                            year = year.toIntOrNull()
                        ))
                        onSaved()
                    }) {
                        Icon(Icons.Default.Save, contentDescription = "Save", tint = WalkmanOrange)
                    }
                }
            )
        },
        containerColor = NearBlack
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // TextFields styled with Walkman Colors
            OrganizerTextField(value = title, onValueChange = { title = it }, label = "TITLE")
            OrganizerTextField(value = artist, onValueChange = { artist = it }, label = "ARTIST")
            OrganizerTextField(value = album, onValueChange = { album = it }, label = "ALBUM")
            OrganizerTextField(value = genre, onValueChange = { genre = it }, label = "GENRE")
            OrganizerTextField(value = year, onValueChange = { year = it }, label = "YEAR")
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text("FILE INFORMATION", style = MaterialTheme.typography.labelSmall, color = WalkmanOrange, letterSpacing = 1.sp)
            Text("PATH: ${song.path}", style = MaterialTheme.typography.bodySmall, color = MetallicGray)
            Text("FORMAT: ${song.format} • SIZE: ${song.size / 1024} KB", style = MaterialTheme.typography.bodySmall, color = MetallicGray)
        }
    }
}

@Composable
fun OrganizerTextField(value: String, onValueChange: (String) -> Unit, label: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = MetallicGray) },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = WalkmanOrange,
            unfocusedBorderColor = Color.DarkGray,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        )
    )
}
