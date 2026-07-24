package com.lemonsquad.musichome.ui.screens

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lemonsquad.musichome.ui.icons.MusicHomeIcons
import com.lemonsquad.musichome.ui.theme.MetallicGray
import com.lemonsquad.musichome.ui.theme.PureBlack
import com.lemonsquad.musichome.ui.theme.WalkmanOrange
import com.lemonsquad.musichome.ui.models.DeviceState
import com.lemonsquad.musichome.ui.models.OutputState
import com.lemonsquad.musichome.ui.viewmodels.MusicUiState
import com.lemonsquad.musichome.ui.viewmodels.MusicViewModel

@Composable
fun AboutScreen(viewModel: MusicViewModel, appVersion: String) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val deviceState by viewModel.deviceState.collectAsState()
    
    val githubUrl = "https://github.com/lemonsquad/musichome"
    val releasesUrl = "$githubUrl/releases"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "SYSTEM INFORMATION",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = WalkmanOrange,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Identity Card
        item {
            AboutCard {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "MUSIC HOME",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 4.sp,
                        color = WalkmanOrange
                    )
                    Text(
                        "Version $appVersion",
                        fontSize = 14.sp,
                        color = MetallicGray,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "\"Old Android devices deserve a second life.\"",
                        fontSize = 14.sp,
                        color = Color.White,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // What's New Card
        item {
            var expanded by remember { mutableStateOf(true) }
            AboutCard(
                title = "WHAT'S NEW",
                onHeaderClick = { expanded = !expanded }
            ) {
                AnimatedVisibility(visible = expanded) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ChangelogItem("1.2.1", listOf(
                            "About Screen redesign",
                            "System Information hub",
                            "Nested Settings navigation",
                            "Programmatic versioning"
                        ))
                    }
                }
            }
        }

        // Updates Card
        item {
            AboutCard(title = "UPDATES") {
                Column {
                    UpdateRow("Current Version", appVersion)
                    UpdateRow("Latest Release", "1.2.1") // Simulated check
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(releasesUrl))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = WalkmanOrange),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Icon(MusicHomeIcons.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("DOWNLOAD LATEST", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Project Card
        item {
            AboutCard(title = "PROJECT") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ProjectLink("GitHub Repository", githubUrl)
                    ProjectLink("MIT License", "$githubUrl/blob/main/LICENSE")
                    ProjectLink("Open Source Notice", "$githubUrl#readme")
                }
            }
        }

        // Hardware Manifest Card
        item {
            AboutCard(title = "HARDWARE MANIFEST") {
                Column {
                    SystemRow("Audio Engine", "Direct Bypass (Simulated)")
                    SystemRow("Output Device", when(deviceState.output) {
                        is OutputState.UsbDAC -> "USB DAC"
                        is OutputState.Bluetooth -> "Bluetooth"
                        is OutputState.InternalDAC -> "Internal DAC"
                        is OutputState.Speaker -> "Phone Speaker"
                    })
                    SystemRow("Gain Stage", deviceState.gain.name)
                    SystemRow("Verification", deviceState.verification.name)
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Color.DarkGray, thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    SystemRow("Power", "${deviceState.power.batteryPercent}% ${if (deviceState.power.isCharging) "(Charging)" else ""}")
                    SystemRow("Network", if (deviceState.network.isWifiConnected) "Wi-Fi Connected" else "Offline")
                }
            }
        }

        // System Card
        item {
            AboutCard(title = "SYSTEM") {
                Column {
                    SystemRow("Android Version", Build.VERSION.RELEASE)
                    SystemRow("Kernel", System.getProperty("os.version") ?: "Unknown")
                    SystemRow("Media3", "1.10.1")
                    SystemRow("Database", "Room 2.8.4")
                    
                    if (uiState is MusicUiState.Success) {
                        val state = uiState as MusicUiState.Success
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = Color.DarkGray, thickness = 0.5.dp)
                        Spacer(modifier = Modifier.height(8.dp))
                        SystemRow("Library Size", "${state.songs.size} Songs")
                        SystemRow("Albums", "${state.albums.size}")
                        SystemRow("Artists", "${state.artists.size}")
                    }
                }
            }
        }

        // Credits Card
        item {
            AboutCard {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("CREATED BY", fontSize = 10.sp, color = MetallicGray, letterSpacing = 2.sp)
                    Text("LEMON SQUAD", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
        
        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

@Composable
fun AboutCard(
    title: String? = null,
    onHeaderClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.DarkGray, RoundedCornerShape(4.dp))
            .background(Color(0xFF0A0A0A))
            .padding(16.dp)
    ) {
        if (title != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (onHeaderClick != null) Modifier.clickable { onHeaderClick() } else Modifier),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MetallicGray,
                    letterSpacing = 1.sp,
                    modifier = Modifier.weight(1f)
                )
                if (onHeaderClick != null) {
                    Icon(MusicHomeIcons.CaretDown, null, tint = MetallicGray, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color.DarkGray, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(12.dp))
        }
        content()
    }
}

@Composable
fun ChangelogItem(version: String, changes: List<String>) {
    Column {
        Text("v$version", fontWeight = FontWeight.Bold, color = WalkmanOrange, fontSize = 14.sp)
        changes.forEach { change ->
            Row(modifier = Modifier.padding(start = 8.dp, top = 4.dp)) {
                Text("•", color = WalkmanOrange, modifier = Modifier.width(16.dp))
                Text(change, color = Color.White, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun UpdateRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MetallicGray, fontSize = 13.sp)
        Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

@Composable
fun SystemRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MetallicGray, fontSize = 12.sp)
        Text(value, color = Color.LightGray, fontSize = 12.sp)
    }
}

@Composable
fun ProjectLink(label: String, url: String) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(MusicHomeIcons.Link, null, tint = WalkmanOrange, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, color = Color.White, fontSize = 13.sp, modifier = Modifier.weight(1f))
        Icon(MusicHomeIcons.ExternalLink, null, tint = MetallicGray, modifier = Modifier.size(14.dp))
    }
}
