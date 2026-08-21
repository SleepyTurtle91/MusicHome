package com.lemonsquad.musichome.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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

import androidx.compose.foundation.border
import com.lemonsquad.musichome.ui.viewmodels.MusicViewModel
import androidx.compose.ui.draw.clip

@Composable
fun AppearanceSettingsScreen(
    viewModel: MusicViewModel,
    onBack: () -> Unit
) {
    val currentAccent by viewModel.accentColor.collectAsState()
    val trueBlack by viewModel.trueBlack.collectAsState()
    val activeAccentColor = Color(currentAccent)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (trueBlack) PureBlack else Color(0xFF09090B))
            .padding(16.dp)
    ) {
        Text(
            text = "APPEARANCE",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = activeAccentColor,
            letterSpacing = 2.sp
        )
        
        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                AppearanceCard(title = "ACCENT COLOR") {
                    val colors = listOf(
                        0xFFFF6A00.toInt() to "Orange",
                        0xFF2196F3.toInt() to "Blue",
                        0xFF4CAF50.toInt() to "Green",
                        0xFFE91E63.toInt() to "Pink"
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        colors.forEach { (colorVal, _) ->
                            val color = Color(colorVal)
                            val isSelected = currentAccent == colorVal
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .then(
                                        if (isSelected) Modifier.border(3.dp, Color.White, CircleShape)
                                        else Modifier
                                    )
                                    .clickable { viewModel.setAccentColor(colorVal) }
                            )
                        }
                    }
                }
            }

            item {
                AppearanceCard(title = "OLED OPTIMIZATION") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("True Black Mode", color = Color.White, fontSize = 14.sp)
                        Switch(
                            checked = trueBlack, 
                            onCheckedChange = { viewModel.setTrueBlack(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = activeAccentColor,
                                checkedTrackColor = activeAccentColor.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = activeAccentColor),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text("DONE", fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}

@Composable
private fun AppearanceCard(title: String, content: @Composable ColumnScope.() -> Unit) {
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
