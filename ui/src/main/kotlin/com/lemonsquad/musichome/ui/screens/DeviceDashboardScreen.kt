package com.lemonsquad.musichome.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lemonsquad.musichome.ui.models.*
import com.lemonsquad.musichome.ui.theme.MetallicGray
import com.lemonsquad.musichome.ui.theme.PureBlack
import com.lemonsquad.musichome.ui.theme.WalkmanOrange
import com.lemonsquad.musichome.ui.viewmodels.MusicViewModel

@Composable
fun DeviceDashboardScreen(
    viewModel: MusicViewModel,
    onBack: () -> Unit
) {
    val deviceState by viewModel.deviceState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
            .padding(16.dp)
    ) {
        Text(
            text = "HARDWARE COCKPIT",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = WalkmanOrange,
            letterSpacing = 2.sp
        )
        
        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                CockpitCard(title = "AUDIO ENGINE") {
                    TechnicalRow("Source", "${deviceState.playback.format ?: "PCM"} ${deviceState.playback.sampleRate?.let { "${it/1000}kHz" } ?: ""}")
                    TechnicalRow("Output", deviceState.output.javaClass.simpleName.replace("OutputState$", ""))
                    TechnicalRow("Verification", deviceState.verification.name)
                    TechnicalRow("Gain Stage", deviceState.gain.name)
                }
            }

            item {
                CockpitCard(title = "DEVICE METRICS") {
                    TechnicalRow("Battery", "${deviceState.power.batteryPercent}% ${if (deviceState.power.isCharging) "⚡" else ""}")
                    TechnicalRow("Network", if (deviceState.network.isWifiConnected) "CONNECTED (WIFI)" else "OFFLINE")
                    TechnicalRow("System Mode", deviceState.mode.name)
                }
            }

            item {
                CockpitCard(title = "PHYSICAL EXPERIENCE") {
                    SettingToggle("Hardware Haptics", deviceState.settings.hapticsEnabled) { 
                        viewModel.updateSettings(deviceState.settings.copy(hapticsEnabled = it))
                    }
                    SettingToggle("OLED Pixel Shifting", deviceState.settings.pixelShiftingEnabled) {
                        viewModel.updateSettings(deviceState.settings.copy(pixelShiftingEnabled = it))
                    }
                    SettingToggle("Volume Safety Limit", deviceState.settings.volumeSafetyEnabled) {
                        viewModel.updateSettings(deviceState.settings.copy(volumeSafetyEnabled = it))
                    }
                }
            }

            item {
                CockpitCard(title = "CAPABILITIES") {
                    TechnicalRow("Max Sample Rate", "${deviceState.capabilities.maxSampleRate / 1000}kHz")
                    TechnicalRow("DSD Support", if (deviceState.capabilities.supportsDsd) "YES" else "NO")
                    TechnicalRow("External DAC", if (deviceState.capabilities.externalDacDetected) "DETECTED" else "NONE")
                }
            }

            item {
                CockpitCard(title = "LAST LISTENING SESSION") {
                    val session = deviceState.session
                    TechnicalRow("Peak Format", "${deviceState.playback.format ?: "PCM"} ${deviceState.playback.sampleRate?.let { "${it/1000}kHz" } ?: ""}")
                    TechnicalRow("Output Device", deviceState.output.javaClass.simpleName.replace("OutputState$", ""))
                    TechnicalRow("Session Start", if (session.startedAt > 0) "Verified" else "Now Active")
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
            Text("CLOSE DASHBOARD", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SettingToggle(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = MetallicGray, fontSize = 12.sp)
        Switch(
            checked = checked, 
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = WalkmanOrange,
                checkedTrackColor = WalkmanOrange.copy(alpha = 0.3f)
            )
        )
    }
}

@Composable
private fun CockpitCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
            .background(Color(0xFF0A0A0A))
            .padding(16.dp)
    ) {
        Text(title, fontSize = 10.sp, color = MetallicGray, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
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
