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

@Composable
fun AppearanceSettingsScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
            .padding(16.dp)
    ) {
        Text(
            text = "APPEARANCE",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = WalkmanOrange,
            letterSpacing = 2.sp
        )
        
        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                AppearanceCard(title = "ACCENT COLOR") {
                    val colors = listOf(WalkmanOrange, Color(0xFF2196F3), Color(0xFF4CAF50), Color(0xFFE91E63))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        colors.forEach { color ->
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(color, CircleShape)
                                    .clickable { /* Update global theme later */ }
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
                            checked = true, 
                            onCheckedChange = {},
                            colors = SwitchDefaults.colors(checkedThumbColor = WalkmanOrange)
                        )
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
