package com.lemonsquad.musichome.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lemonsquad.musichome.organizer.ui.LibraryToolsViewModel
import com.lemonsquad.musichome.ui.theme.WalkmanOrange
import com.lemonsquad.musichome.ui.theme.NearBlack
import com.lemonsquad.musichome.ui.theme.DarkSurface
import com.lemonsquad.musichome.ui.theme.MetallicGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerProgressScreen(
    viewModel: LibraryToolsViewModel,
    onBack: () -> Unit
) {
    val isScanning by viewModel.isScanning.collectAsState()
    val songs by viewModel.songs.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("SYSTEM SCAN", style = MaterialTheme.typography.titleMedium, letterSpacing = 2.sp) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = NearBlack)
            )
        },
        containerColor = NearBlack
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .border(2.dp, if (isScanning) WalkmanOrange else Color.DarkGray, RoundedCornerShape(120.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (isScanning) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(200.dp),
                        color = WalkmanOrange,
                        strokeWidth = 4.dp
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        if (isScanning) "SCANNING..." else "IDLE",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isScanning) WalkmanOrange else MetallicGray
                    )
                    Text(
                        "${songs.size}",
                        style = MaterialTheme.typography.displayMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text("FILES DETECTED", fontSize = 10.sp, color = MetallicGray)
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface, RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Text("ENGINE STATUS: ONLINE", fontSize = 10.sp, color = Color.Green)
                Text("DATA SOURCE: MEDIASTORE / LOCAL", fontSize = 10.sp, color = MetallicGray)
                Spacer(modifier = Modifier.height(16.dp))
                if (!isScanning) {
                    Button(
                        onClick = { viewModel.scanLibrary() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = WalkmanOrange)
                    ) {
                        Text("INITIATE DEEP SCAN", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Text("READING METADATA TABLES...", fontSize = 12.sp, color = WalkmanOrange)
                }
            }
            
            if (!isScanning) {
                TextButton(onClick = onBack, modifier = Modifier.padding(top = 16.dp)) {
                    Text("RETURN TO DASHBOARD", color = MetallicGray)
                }
            }
        }
    }
}
