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

@Composable
fun UpdatesSettingsScreen(appVersion: String, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
            .padding(16.dp)
    ) {
        Text(
            text = "SYSTEM UPDATES",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = WalkmanOrange,
            letterSpacing = 2.sp
        )
        
        Spacer(modifier = Modifier.height(24.dp))

        UpdateCard {
            Text("Current Version", color = MetallicGray, fontSize = 10.sp)
            Text("v$appVersion", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Last Checked", color = MetallicGray, fontSize = 10.sp)
            Text("July 29, 2026", color = Color.White, fontSize = 14.sp)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = WalkmanOrange),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text("CHECK NOW", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        UpdateCard(title = "RELEASE NOTES") {
            Text(
                "• Implemented DAP-grade hardware metaphors\n• Added High-Honesty signal path visualization\n• Optimized for OLED legacy hardware",
                color = Color.LightGray,
                fontSize = 13.sp,
                lineHeight = 20.sp
            )
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
private fun UpdateCard(title: String? = null, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0A0A0A), RoundedCornerShape(4.dp))
            .padding(16.dp)
    ) {
        if (title != null) {
            Text(title, fontSize = 10.sp, color = MetallicGray, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
        }
        content()
    }
}
