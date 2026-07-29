package com.lemonsquad.musichome.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lemonsquad.musichome.organizer.ui.LibraryToolsViewModel
import com.lemonsquad.musichome.core.domain.analysis.DuplicateGroup
import com.lemonsquad.musichome.ui.theme.WalkmanOrange
import com.lemonsquad.musichome.ui.theme.NearBlack
import com.lemonsquad.musichome.ui.theme.DarkSurface
import com.lemonsquad.musichome.ui.theme.MetallicGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuplicateFinderScreen(
    viewModel: LibraryToolsViewModel,
    onBack: () -> Unit
) {
    val groups by viewModel.duplicateGroups.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("DUPLICATE FINDER", style = MaterialTheme.typography.titleMedium, letterSpacing = 2.sp) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = NearBlack)
            )
        },
        containerColor = NearBlack
    ) { padding ->
        if (groups.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("NO DUPLICATES DETECTED", color = MetallicGray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(groups) { group ->
                    DuplicateGroupCard(group)
                }
            }
        }
    }
}

@Composable
fun DuplicateGroupCard(group: DuplicateGroup) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkSurface, MaterialTheme.shapes.medium)
            .padding(16.dp)
    ) {
        Text(
            group.songs.firstOrNull()?.title ?: "Unknown",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 18.sp
        )
        Text(
            "${group.songs.size} COPIES FOUND",
            color = WalkmanOrange,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = Color.DarkGray)
        Spacer(modifier = Modifier.height(8.dp))

        group.songs.forEach { song ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(song.path, color = Color.White, fontSize = 11.sp, maxLines = 1)
                    Text("${song.mimeType} • ${song.size / 1024} KB", color = MetallicGray, fontSize = 10.sp)
                }
                IconButton(onClick = { /* Delete action */ }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
