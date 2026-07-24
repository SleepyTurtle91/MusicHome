package com.lemonsquad.musichome.ui.components.sound

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lemonsquad.musichome.ui.theme.MetallicGray
import com.lemonsquad.musichome.ui.theme.WalkmanOrange

@Composable
fun GainSelector(
    selectedGain: String,
    onGainSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf("LOW", "MID", "HIGH")
    
    Column(modifier = modifier) {
        Text(
            text = "GAIN STAGE (DISCRETE AMPLIFICATION)",
            color = MetallicGray,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                val isSelected = selectedGain == option
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .background(
                            color = if (isSelected) WalkmanOrange.copy(alpha = 0.2f) else Color(0xFF151515),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .clickable { onGainSelected(option) }
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isSelected) "• $option •" else option,
                        color = if (isSelected) WalkmanOrange else Color.White,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}
