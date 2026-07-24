package com.lemonsquad.musichome.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.lemonsquad.musichome.ui.models.*
import com.lemonsquad.musichome.ui.viewmodels.MusicViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lemonsquad.musichome.ui.icons.MusicHomeIcons
import com.lemonsquad.musichome.ui.theme.MetallicGray
import com.lemonsquad.musichome.ui.theme.PureBlack
import com.lemonsquad.musichome.ui.theme.WalkmanOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MusicViewModel,
    onNavigateToAbout: () -> Unit,
    onNavigateToDashboard: () -> Unit
) {
    var developerTaps by remember { mutableIntStateOf(0) }
    var showDiagnostics by remember { mutableStateOf(false) }

    val settingsItems = mutableListOf(
        SettingsItem("Appearance", MusicHomeIcons.Appearance),
        SettingsItem("Hardware Cockpit", MusicHomeIcons.Tools, onClick = onNavigateToDashboard),
        SettingsItem("Playback", MusicHomeIcons.Playback),
        SettingsItem("Library", MusicHomeIcons.Library),
        SettingsItem("Updates", MusicHomeIcons.Update),
        SettingsItem("About Music Home", MusicHomeIcons.Info, onClick = onNavigateToAbout)
    )

    if (developerTaps >= 7) {
        settingsItems.add(SettingsItem("Device Diagnostics", MusicHomeIcons.Tools, onClick = { showDiagnostics = true }))
    }

    if (showDiagnostics) {
        val deviceState by viewModel.deviceState.collectAsState()
        DeviceStateDebugPanel(state = deviceState, onDismiss = { showDiagnostics = false })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
    ) {
        Text(
            "SETTINGS",
            modifier = Modifier
                .padding(16.dp)
                .clickable { developerTaps++ },
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = WalkmanOrange,
            letterSpacing = 2.sp
        )

        LazyColumn {
            items(settingsItems) { item ->
                SettingsRow(item)
                HorizontalDivider(color = Color.DarkGray, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

@Composable
fun DeviceStateDebugPanel(state: DeviceState, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("HARDWARE DIAGNOSTICS", fontSize = 14.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                DebugRow("Playback", if (state.playback.isPlaying) "PLAYING" else "IDLE")
                DebugRow("Output", state.output.javaClass.simpleName)
                DebugRow("Format", "${state.playback.format ?: "N/A"} ${state.playback.sampleRate?.let { "${it/1000}kHz" } ?: ""}")
                DebugRow("Verification", state.verification.name)
                DebugRow("LED State", state.audioState.name)
                DebugRow("Gain", state.gain.name)
                DebugRow("Library", state.scanState.javaClass.simpleName)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("CLOSE", color = WalkmanOrange) }
        },
        containerColor = Color(0xFF151515),
        titleContentColor = Color.White,
        textContentColor = Color.LightGray
    )
}

@Composable
private fun DebugRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = MetallicGray, fontSize = 12.sp)
        Text(text = value, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SettingsRow(item: SettingsItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { item.onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = null,
            tint = MetallicGray,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = item.title,
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = MusicHomeIcons.CaretRight,
            contentDescription = null,
            tint = Color.DarkGray,
            modifier = Modifier.size(16.dp)
        )
    }
}

data class SettingsItem(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit = {}
)
