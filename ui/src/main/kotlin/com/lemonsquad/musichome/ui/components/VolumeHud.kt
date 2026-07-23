package com.lemonsquad.musichome.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lemonsquad.musichome.ui.icons.MusicHomeIcons
import com.lemonsquad.musichome.ui.theme.WalkmanOrange

@Composable
fun VolumeHud(
    visible: Boolean,
    volume: Int,
    maxVolume: Int
) {
    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + slideInHorizontally { it },
            exit = fadeOut() + slideOutHorizontally { it },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 32.dp)
        ) {
            Column(
                modifier = Modifier
                    .width(64.dp)
                    .height(250.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color.Black.copy(alpha = 0.8f))
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                val icon = when {
                    volume == 0 -> MusicHomeIcons.VolumeMute
                    volume < maxVolume / 2 -> MusicHomeIcons.VolumeLow
                    else -> MusicHomeIcons.VolumeHigh
                }
                
                Icon(
                    icon,
                    contentDescription = null,
                    tint = WalkmanOrange,
                    modifier = Modifier.size(24.dp)
                )

                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .weight(1f)
                        .padding(vertical = 16.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.DarkGray)
                ) {
                    val progress = if (maxVolume > 0) volume.toFloat() / maxVolume else 0f
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(progress)
                            .align(Alignment.BottomCenter)
                            .background(WalkmanOrange)
                    )
                }

                Text(
                    text = volume.toString(),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
