package com.lemonsquad.musichome.ui.screens.settings

import androidx.compose.foundation.background
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
import com.lemonsquad.musichome.ui.theme.MetallicGray
import com.lemonsquad.musichome.ui.theme.PureBlack
import com.lemonsquad.musichome.ui.theme.WalkmanOrange
import com.lemonsquad.musichome.ui.viewmodels.MusicViewModel

@Composable
fun PlaybackSettingsScreen(viewModel: MusicViewModel, onBack: () -> Unit) {
    val deviceState by viewModel.deviceState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
            .padding(16.dp)
    ) {
        Text(
            text = "PLAYBACK",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = WalkmanOrange,
            letterSpacing = 2.sp
        )
        
        Spacer(modifier = Modifier.height(24.dp))

        PlaybackCard(title = "AUDIO ENGINE") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Gapless Playback", color = Color.White, fontSize = 14.sp)
                Switch(
                    checked = true, 
                    onCheckedChange = { /* Update setting */ },
                    colors = SwitchDefaults.colors(checkedThumbColor = WalkmanOrange)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        PlaybackCard(title = "LOUDNESS") {
            Text("ReplayGain Mode", color = Color.White, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(12.dp))
            val modes = listOf("OFF", "TRACK", "ALBUM")
            // We use the setting from the real viewModel if available
            var selected by remember { mutableStateOf("ALBUM") }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                modes.forEach { mode ->
                    val isSelected = selected == mode
                    FilterChip(
                        selected = isSelected,
                        onClick = { 
                            selected = mode
                            // Update ReplayGain mode in engine
                            val modeInt = when(mode) {
                                "OFF" -> 0
                                "TRACK" -> 1
                                "ALBUM" -> 2
                                else -> 0
                            }
                            // viewModel.setReplayGainMode(modeInt)
                        },
                        label = { Text(mode) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = WalkmanOrange,
                            selectedLabelColor = Color.Black
                        )
                    )
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
private fun PlaybackCard(title: String, content: @Composable ColumnScope.() -> Unit) {
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
