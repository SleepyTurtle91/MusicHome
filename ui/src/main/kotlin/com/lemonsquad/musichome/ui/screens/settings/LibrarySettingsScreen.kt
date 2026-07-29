package com.lemonsquad.musichome.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lemonsquad.musichome.ui.icons.MusicHomeIcons
import com.lemonsquad.musichome.ui.theme.MetallicGray
import com.lemonsquad.musichome.ui.theme.PureBlack
import com.lemonsquad.musichome.ui.theme.WalkmanOrange
import com.lemonsquad.musichome.ui.viewmodels.MusicViewModel
import com.lemonsquad.musichome.ui.viewmodels.MusicUiState

@Composable
fun LibrarySettingsScreen(
    viewModel: MusicViewModel,
    onBack: () -> Unit
) {
    val folders by viewModel.repository.watchedFolders.collectAsState(initial = emptyList())
    val stats by viewModel.repository.libraryStats.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
            .padding(16.dp)
    ) {
        Text(
            text = "LIBRARY CONFIGURATION",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = WalkmanOrange,
            letterSpacing = 2.sp
        )
        
        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Stats Card
            item {
                LibraryCard(title = "STATISTICS") {
                    TechnicalRow("Total Songs", stats.totalSongs.toString())
                    TechnicalRow("Total Albums", stats.totalAlbums.toString())
                    TechnicalRow("Total Artists", stats.totalArtists.toString())
                    if (stats.lastScanTimestamp > 0) {
                        TechnicalRow("Last Scan", java.text.SimpleDateFormat("MMM dd, HH:mm", java.util.Locale.getDefault()).format(stats.lastScanTimestamp))
                    }
                }
            }

            // Folders Card
            item {
                LibraryCard(title = "MUSIC LOCATIONS") {
                    Button(
                        onClick = { viewModel.requestDirectoryPicker() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = WalkmanOrange),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("ADD FOLDER", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (folders.isEmpty()) {
                        Text("No folders added", color = MetallicGray, fontSize = 12.sp)
                    } else {
                        folders.forEach { folder ->
                            FolderRow(
                                path = folder,
                                onRemove = { viewModel.repository.removeManualPath(folder) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            // Actions Card
            item {
                LibraryCard(title = "ACTIONS") {
                    Button(
                        onClick = { viewModel.syncLibrary() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("FORCE FULL SCAN", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text("DONE", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun FolderRow(path: String, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(4.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = path.substringAfterLast("/"), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(text = path, color = MetallicGray, fontSize = 10.sp, maxLines = 1)
        }
        IconButton(onClick = onRemove) {
            Icon(MusicHomeIcons.Delete, null, tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun LibraryCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0A0A0A), RoundedCornerShape(4.dp))
            .padding(16.dp)
    ) {
        Text(title, fontSize = 10.sp, color = MetallicGray, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}

@Composable
private fun TechnicalRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MetallicGray, fontSize = 12.sp)
        Text(value, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}
