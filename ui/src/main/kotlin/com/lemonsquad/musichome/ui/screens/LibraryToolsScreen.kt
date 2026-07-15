package com.lemonsquad.musichome.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lemonsquad.musichome.organizer.ui.LibraryToolsViewModel
import com.lemonsquad.musichome.ui.theme.WalkmanOrange
import com.lemonsquad.musichome.ui.theme.DarkSurface
import com.lemonsquad.musichome.ui.theme.MetallicGray

@Composable
fun LibraryToolsScreen(
    viewModel: LibraryToolsViewModel,
    onNavigateToDuplicates: () -> Unit,
    onNavigateToScanner: () -> Unit
) {
    val stats by viewModel.healthStats.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            HealthPanel(stats)
        }

        item {
            Text(
                "MAINTENANCE TOOLS",
                style = MaterialTheme.typography.labelMedium,
                color = WalkmanOrange,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        item {
            ToolCard(
                title = "MEDIA SCANNER",
                description = "Deep scan local folders for new or updated files.",
                icon = Icons.Default.Refresh,
                onClick = onNavigateToScanner
            )
        }

        item {
            ToolCard(
                title = "DUPLICATE FINDER",
                description = "Locate and manage identical song entries.",
                icon = Icons.Default.FilterNone,
                onClick = onNavigateToDuplicates
            )
        }

        item {
            ToolCard(
                title = "TAG EDITOR",
                description = "Manual metadata correction for the library.",
                icon = Icons.Default.Edit,
                onClick = { /* Navigate to a list of songs or something */ }
            )
        }
    }
}

@Composable
fun HealthPanel(stats: com.lemonsquad.musichome.organizer.health.LibraryHealthStats) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkSurface, RoundedCornerShape(8.dp))
            .border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "LIBRARY HEALTH",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                "${stats.score}%",
                style = MaterialTheme.typography.headlineMedium,
                color = if (stats.score > 80) Color.Green else if (stats.score > 50) WalkmanOrange else Color.Red,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        
        LinearProgressIndicator(
            progress = { stats.score / 100f },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = WalkmanOrange,
            trackColor = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            StatItem("SONGS", stats.totalSongs.toString(), Modifier.weight(1f))
            StatItem("ALBUMS", stats.totalAlbums.toString(), Modifier.weight(1f))
            StatItem("ARTISTS", stats.totalArtists.toString(), Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(12.dp))

        HorizontalDivider(color = Color.DarkGray)

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            AlertItem("MISSING ART", stats.missingArtworkCount.toString(), stats.missingArtworkCount > 0)
            Spacer(modifier = Modifier.width(16.dp))
            AlertItem("DUPLICATES", stats.duplicateCount.toString(), stats.duplicateCount > 0)
        }
    }
}

@Composable
fun StatItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(label, fontSize = 10.sp, color = MetallicGray)
    }
}

@Composable
fun AlertItem(label: String, value: String, isWarning: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(if (isWarning) WalkmanOrange else Color.Green, RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text("$label: ", fontSize = 10.sp, color = MetallicGray)
        Text(value, fontSize = 10.sp, color = if (isWarning) Color.White else Color.Green)
    }
}

@Composable
fun ToolCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color.DarkGray))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = WalkmanOrange, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, color = Color.White)
                Text(description, fontSize = 12.sp, color = MetallicGray)
            }
        }
    }
}
