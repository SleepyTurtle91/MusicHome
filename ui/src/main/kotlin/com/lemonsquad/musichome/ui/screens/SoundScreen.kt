package com.lemonsquad.musichome.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.lemonsquad.musichome.core.domain.model.EqualizerSettings
import com.lemonsquad.musichome.ui.components.sound.GainSelector
import com.lemonsquad.musichome.ui.components.sound.SignalChainCard
import com.lemonsquad.musichome.ui.models.GainStage
import com.lemonsquad.musichome.ui.models.OutputState
import com.lemonsquad.musichome.ui.theme.MetallicGray
import com.lemonsquad.musichome.ui.theme.PureBlack
import com.lemonsquad.musichome.ui.theme.WalkmanOrange
import com.lemonsquad.musichome.ui.viewmodels.MusicViewModel

@Composable
fun SoundScreen(viewModel: MusicViewModel) {
    val eqEnabled by viewModel.eqEnabled.collectAsState()
    val eqBands by viewModel.eqBands.collectAsState()
    
    val bands = listOf("60Hz", "230Hz", "910Hz", "3.6kHz", "14kHz")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        // Header LED & Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SOUND",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp
                )
                Text(
                    text = "HARDWARE CALIBRATION",
                    color = WalkmanOrange,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Trust Card / Signal Chain
        val deviceState by viewModel.deviceState.collectAsState()
        val playbackStatus = deviceState.playback
        val sourceInfo = "${playbackStatus.format ?: "PCM"} ${playbackStatus.sampleRate?.let { "${it/1000}kHz" } ?: ""}"
        
        val outputName = when (deviceState.output) {
            is OutputState.UsbDAC -> "USB DAC"
            is OutputState.Bluetooth -> "Bluetooth"
            is OutputState.Speaker -> "Speaker"
            is OutputState.InternalDAC -> "Internal DAC"
        }

        SignalChainCard(
            source = sourceInfo,
            engine = "Direct Bypass",
            output = outputName,
            verificationStatus = deviceState.verification
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Gain Selector
        GainSelector(
            selectedGain = deviceState.gain.name,
            onGainSelected = { viewModel.setGainStage(GainStage.valueOf(it)) }
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Precision Equalizer
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PRECISION EQUALIZER",
                color = MetallicGray,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            
            Switch(
                checked = eqEnabled,
                onCheckedChange = { viewModel.setEqEnabled(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = WalkmanOrange,
                    checkedTrackColor = WalkmanOrange.copy(alpha = 0.3f),
                )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Tactile EQ Sliders (Simplified in main view)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            bands.forEachIndexed { index, freq ->
                val level = eqBands[index] ?: 0
                EqSlider(
                    label = freq, 
                    enabled = eqEnabled,
                    value = (level + 1500) / 3000f, 
                    onValueChange = { newValue ->
                        val newLevel = (newValue * 3000 - 1500).toInt()
                        viewModel.setEqBandLevel(index, newLevel)
                    }
                )
            }
        }

        // Preset Selector
        Text(
            text = "EQUALIZER PRESETS",
            color = MetallicGray,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        val presets = EqualizerSettings.PRESETS.keys.toList()
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(presets) { preset ->
                AssistChip(
                    onClick = { viewModel.applyEqPreset(preset) },
                    label = { Text(preset, fontSize = 10.sp) },
                    colors = AssistChipDefaults.assistChipColors(
                        labelColor = Color.White,
                        containerColor = Color.Transparent
                    ),
                    border = AssistChipDefaults.assistChipBorder(
                        enabled = true,
                        borderColor = WalkmanOrange
                    ),
                    shape = RoundedCornerShape(4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Sleep Timer
        Text(
            text = "SLEEP TIMER",
            color = MetallicGray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        val timerOptions = listOf(0, 15, 30, 45, 60)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(timerOptions) { mins ->
                AssistChip(
                    onClick = { viewModel.setSleepTimer(mins) },
                    label = { Text(if (mins == 0) "OFF" else "$mins MIN", fontSize = 10.sp) },
                    colors = AssistChipDefaults.assistChipColors(
                        labelColor = Color.White,
                        containerColor = Color.Transparent
                    ),
                    border = AssistChipDefaults.assistChipBorder(
                        enabled = true,
                        borderColor = Color.DarkGray
                    ),
                    shape = RoundedCornerShape(4.dp)
                )
            }
        }
    }
}

@Composable
fun EqSlider(
    label: String, 
    enabled: Boolean,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxHeight()
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .width(40.dp),
            contentAlignment = Alignment.Center
        ) {
            // Background track
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(4.dp)
                    .background(Color.DarkGray, RoundedCornerShape(2.dp))
            )
            
            Slider(
                value = value,
                onValueChange = onValueChange,
                enabled = enabled,
                modifier = Modifier
                    .graphicsLayer(rotationZ = -90f)
                    .width(200.dp), // Height in vertical mode
                colors = SliderDefaults.colors(
                    thumbColor = if (enabled) WalkmanOrange else MetallicGray,
                    activeTrackColor = Color.Transparent,
                    inactiveTrackColor = Color.Transparent
                )
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = label,
            color = if (enabled) Color.White else MetallicGray,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
