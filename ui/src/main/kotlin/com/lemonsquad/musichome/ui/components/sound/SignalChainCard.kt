package com.lemonsquad.musichome.ui.components.sound

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lemonsquad.musichome.ui.icons.MusicHomeIcons
import com.lemonsquad.musichome.ui.models.VerificationStatus
import com.lemonsquad.musichome.ui.theme.MetallicGray
import com.lemonsquad.musichome.ui.theme.WalkmanOrange

@Composable
fun SignalChainCard(
    source: String,
    engine: String,
    output: String,
    verificationStatus: VerificationStatus,
    modifier: Modifier = Modifier
) {
    val statusText = when (verificationStatus) {
        VerificationStatus.VERIFIED -> "✓ VERIFIED BIT PERFECT"
        VerificationStatus.ESTIMATED -> "◉ ESTIMATED DIRECT PATH"
        VerificationStatus.UNKNOWN -> "? OUTPUT STATUS UNKNOWN"
    }
    
    val statusColor = when (verificationStatus) {
        VerificationStatus.VERIFIED -> WalkmanOrange
        VerificationStatus.ESTIMATED -> Color.Green
        VerificationStatus.UNKNOWN -> MetallicGray
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
        color = Color(0xFF0A0A0A),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = statusText,
                    color = statusColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                
                Text(
                    text = if (verificationStatus == VerificationStatus.VERIFIED) "HARDWARE PATH" else "SOFTWARE PATH",
                    color = MetallicGray,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ChainNode("SOURCE", source)
                ChainConnector()
                ChainNode("ENGINE", engine)
                ChainConnector()
                ChainNode("OUTPUT", output)
            }
        }
    }
}

@Composable
private fun ChainNode(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = MetallicGray, fontSize = 8.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun ChainConnector() {
    Box(
        modifier = Modifier
            .width(20.dp)
            .height(1.dp)
            .background(Color.White.copy(alpha = 0.2f))
    )
}
