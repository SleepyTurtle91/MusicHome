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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lemonsquad.musichome.ui.theme.MetallicGray
import com.lemonsquad.musichome.ui.theme.PureBlack
import com.lemonsquad.musichome.ui.theme.WalkmanOrange
import com.lemonsquad.musichome.ui.viewmodels.MusicViewModel

@Composable
fun SoundScreen(viewModel: MusicViewModel) {
    // Placeholder data for the UI-only phase of EQ
    val bands = listOf("60Hz", "230Hz", "910Hz", "3.6kHz", "14kHz")
    var eqEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SOUND SCULPTING",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "PRECISION EQUALIZER",
                    color = WalkmanOrange,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp
                )
            }
            
            Switch(
                checked = eqEnabled,
                onCheckedChange = { eqEnabled = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = WalkmanOrange,
                    checkedTrackColor = WalkmanOrange.copy(alpha = 0.3f),
                    uncheckedThumbColor = MetallicGray,
                    uncheckedTrackColor = Color.DarkGray
                )
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Tactile EQ Sliders
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            bands.forEach { freq ->
                EqSlider(label = freq, enabled = eqEnabled)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Preset Selector
        Text(
            text = "PRESETS",
            color = MetallicGray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        val presets = listOf("FLAT", "ROCK", "POP", "JAZZ", "BASS BOOST")
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(presets) { preset ->
                AssistChip(
                    onClick = { /* TODO */ },
                    label = { Text(preset, fontSize = 10.sp) },
                    colors = AssistChipDefaults.assistChipColors(
                        labelColor = if (preset == "FLAT") WalkmanOrange else Color.White,
                        containerColor = if (preset == "FLAT") Color(0xFF1A1A1A) else Color.Transparent
                    ),
                    border = AssistChipDefaults.assistChipBorder(
                        borderColor = if (preset == "FLAT") WalkmanOrange else Color.DarkGray
                    ),
                    shape = RoundedCornerShape(4.dp)
                )
            }
        }
    }
}

@Composable
fun EqSlider(label: String, enabled: Boolean) {
    var sliderValue by remember { mutableStateOf(0.5f) }

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
                value = sliderValue,
                onValueChange = { sliderValue = it },
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

// Add this to avoid unresolved reference in Slider rotation
@Composable
fun Modifier.graphicsLayer(rotationZ: Float): Modifier = this.then(
    androidx.compose.ui.graphics.graphicsLayer(rotationZ = rotationZ)
)
