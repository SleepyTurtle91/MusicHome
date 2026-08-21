package com.lemonsquad.musichome.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lemonsquad.musichome.core.domain.model.Song
import com.lemonsquad.musichome.ui.theme.MetallicGray
import com.lemonsquad.musichome.ui.theme.PureBlack
import com.lemonsquad.musichome.ui.theme.WalkmanOrange
import com.lemonsquad.musichome.ui.viewmodels.MusicViewModel

@Composable
fun QueueScreen(viewModel: MusicViewModel) {
    val queue by viewModel.repository.currentQueue.collectAsState()
    val haptic = LocalHapticFeedback.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.AutoMirrored.Filled.QueueMusic,
                contentDescription = null,
                tint = WalkmanOrange,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "PLAYBACK QUEUE",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                queue?.sourceName?.let {
                    Text(
                        text = "FROM: ${it.uppercase()}",
                        color = WalkmanOrange,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        if (queue == null || queue!!.songs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("QUEUE IS EMPTY", color = MetallicGray, letterSpacing = 2.sp)
            }
        } else {
            val q = queue!!
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item {
                    Text(
                        text = "NOW PLAYING",
                        color = MetallicGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                        letterSpacing = 2.sp
                    )
                    QueueSongItem(
                        song = q.songs[q.currentIndex],
                        isCurrent = true,
                        onClick = {},
                        onRemove = { viewModel.removeQueueItem(q.currentIndex) }
                    )
                    
                    if (q.currentIndex < q.songs.size - 1) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "UP NEXT",
                            color = MetallicGray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                            letterSpacing = 2.sp
                        )
                    }
                }

                itemsIndexed(q.songs, key = { _, song -> song.id }) { index, song ->
                    if (index > q.currentIndex) {
                        SwipeToDismissItem(
                            onDismiss = { 
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.removeQueueItem(index) 
                            },
                            content = {
                                QueueSongItem(
                                    song = song,
                                    isCurrent = false,
                                    onClick = { viewModel.updateQueueIndex(index) },
                                    onRemove = { viewModel.removeQueueItem(index) },
                                    showHandle = true
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDismissItem(
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState()

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            onDismiss()
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            val color = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                Color.Red.copy(alpha = 0.8f)
            } else Color.Transparent
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text("REMOVE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        },
        content = { content() }
    )
}

@Composable
fun QueueSongItem(
    song: Song, 
    isCurrent: Boolean, 
    onClick: () -> Unit,
    onRemove: () -> Unit,
    showHandle: Boolean = false
) {
    Surface(
        onClick = onClick,
        color = if (isCurrent) Color(0xFF1A1A1A) else Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showHandle) {
                Icon(
                    Icons.Default.DragHandle,
                    contentDescription = "Reorder",
                    tint = MetallicGray.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
            }

            Text(
                text = if (isCurrent) "▶" else "",
                color = WalkmanOrange,
                fontSize = 14.sp,
                modifier = Modifier.width(24.dp)
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    color = if (isCurrent) WalkmanOrange else Color.White,
                    fontSize = 16.sp,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium
                )
                Text(text = song.artist, color = MetallicGray, fontSize = 12.sp)
            }
        }
    }
}
