package com.lemonsquad.musichome.ui.screens

import android.util.Log
import android.view.HapticFeedbackConstants
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.lemonsquad.musichome.core.domain.model.Song
import com.lemonsquad.musichome.ui.components.SpectrumVisualizer
import com.lemonsquad.musichome.ui.components.VUMeter
import com.lemonsquad.musichome.ui.components.sound.SignalChainCard
import com.lemonsquad.musichome.ui.icons.MusicHomeIcons
import com.lemonsquad.musichome.ui.models.DeviceState
import com.lemonsquad.musichome.ui.models.VerificationStatus
import com.lemonsquad.musichome.ui.theme.MetallicGray
import com.lemonsquad.musichome.ui.theme.PureBlack
import com.lemonsquad.musichome.ui.theme.WalkmanOrange
import com.lemonsquad.musichome.ui.viewmodels.MusicUiState
import com.lemonsquad.musichome.ui.viewmodels.MusicViewModel
import com.lemonsquad.musichome.ui.utils.findActivity
import kotlinx.coroutines.delay
import kotlin.math.abs

enum class VisualizerMode {
    SPECTRUM, VU_METER, OFF
}

@Composable
fun PlayerScreen(viewModel: MusicViewModel) {
    val uiState by viewModel.uiState.collectAsState(initial = MusicUiState.Loading)
    val spectrum by viewModel.spectrum.collectAsState(initial = FloatArray(16))
    val deviceState by viewModel.deviceState.collectAsState()
    val palette by viewModel.currentPalette.collectAsState()
    
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val isPortrait = configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT
    val isTablet = configuration.screenWidthDp >= 600
    
    var isArtworkFocusMode by remember { mutableStateOf(false) }
    var lastInteractionTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var showAmbientMode by remember { mutableStateOf(false) }
    var showSignalChain by remember { mutableStateOf(false) }
    var visualizerMode by remember { mutableStateOf(VisualizerMode.SPECTRUM) }

    val dominantAnimate by animateColorAsState(palette.dominant, animationSpec = tween(1000), label = "Dominant")
    val darkVibrantAnimate by animateColorAsState(palette.darkVibrant, animationSpec = tween(1000), label = "Vibrant")

    LaunchedEffect(lastInteractionTime, deviceState.playback.isPlaying) {
        if (deviceState.playback.isPlaying && deviceState.mode != com.lemonsquad.musichome.ui.models.DeviceMode.HARDWARE_LOCKED) {
            delay(30000)
            showAmbientMode = true
        }
    }

    if (showAmbientMode) {
        AmbientPlayer(
            viewModel = viewModel,
            onExit = {
                showAmbientMode = false
                lastInteractionTime = System.currentTimeMillis()
            }
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { lastInteractionTime = System.currentTimeMillis() },
                        onLongPress = {
                            Log.d("PlayerScreen", "Gesture: Long Press (Hardware Lock)")
                            viewModel.toggleDeviceMode(
                                if (deviceState.mode == com.lemonsquad.musichome.ui.models.DeviceMode.HARDWARE_LOCKED) 
                                    com.lemonsquad.musichome.ui.models.DeviceMode.LISTENING 
                                else 
                                    com.lemonsquad.musichome.ui.models.DeviceMode.HARDWARE_LOCKED
                            )
                            context.findActivity()?.window?.decorView?.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                        }
                    )
                }
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            darkVibrantAnimate.copy(alpha = 0.5f),
                            dominantAnimate.copy(alpha = 0.2f),
                            PureBlack
                        )
                    )
                )
        ) {
            when (val state = uiState) {
                is MusicUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = WalkmanOrange)
                }
                is MusicUiState.Empty -> {
                    Text(
                        "NO MEDIA DETECTED",
                        color = MetallicGray,
                        letterSpacing = 2.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is MusicUiState.Success -> {
                    val currentSong = state.songs.find { it.id.toString() == deviceState.playback.currentSongId } ?: state.songs.firstOrNull()
                    
                    if (currentSong != null) {
                        if (isArtworkFocusMode) {
                            ArtworkFocusLayout(currentSong, spectrum, deviceState, viewModel, darkVibrantAnimate, onExit = { isArtworkFocusMode = false })
                        } else {
                            if (isTablet && !isPortrait) {
                                TabletPlayerLayout(
                                    currentSong, 
                                    spectrum, 
                                    deviceState, 
                                    viewModel, 
                                    darkVibrantAnimate, 
                                    onShowSignalChain = { showSignalChain = true },
                                    onArtworkFocusToggle = { isArtworkFocusMode = !isArtworkFocusMode },
                                    visualizerMode = visualizerMode,
                                    onToggleVisualizer = {
                                        visualizerMode = when(visualizerMode) {
                                            VisualizerMode.SPECTRUM -> VisualizerMode.VU_METER
                                            VisualizerMode.VU_METER -> VisualizerMode.OFF
                                            VisualizerMode.OFF -> VisualizerMode.SPECTRUM
                                        }
                                    }
                                )
                            } else {
                                PhonePlayerLayout(
                                    currentSong, 
                                    spectrum, 
                                    deviceState, 
                                    viewModel, 
                                    darkVibrantAnimate,
                                    onShowSignalChain = { showSignalChain = true },
                                    onArtworkFocusToggle = { isArtworkFocusMode = !isArtworkFocusMode },
                                    visualizerMode = visualizerMode,
                                    onToggleVisualizer = {
                                        visualizerMode = when(visualizerMode) {
                                            VisualizerMode.SPECTRUM -> VisualizerMode.VU_METER
                                            VisualizerMode.VU_METER -> VisualizerMode.OFF
                                            VisualizerMode.OFF -> VisualizerMode.SPECTRUM
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            if (showSignalChain) {
                val status = deviceState.playback
                val sourceInfo = "${status.format ?: "PCM"} ${status.sampleRate?.let { "${it/1000}kHz" } ?: ""}"
                AlertDialog(
                    onDismissRequest = { showSignalChain = false },
                    title = { Text("AUDIO SIGNAL CHAIN", fontSize = 14.sp, fontWeight = FontWeight.Bold) },
                    text = {
                        SignalChainCard(
                            source = sourceInfo,
                            engine = "Direct Bypass",
                            output = deviceState.output.javaClass.simpleName.replace("OutputState$", ""),
                            verificationStatus = deviceState.verification
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = { showSignalChain = false }) {
                            Text("CLOSE", color = WalkmanOrange)
                        }
                    },
                    containerColor = Color(0xFF151515),
                    titleContentColor = Color.White
                )
            }
        }
    }
}

@Composable
fun TabletPlayerLayout(
    currentSong: Song,
    spectrum: FloatArray,
    deviceState: DeviceState,
    viewModel: MusicViewModel,
    accentColor: Color,
    onShowSignalChain: () -> Unit,
    onArtworkFocusToggle: () -> Unit,
    visualizerMode: VisualizerMode,
    onToggleVisualizer: () -> Unit
) {
    val status = deviceState.playback
    val context = LocalContext.current
    val artworkScale by animateFloatAsState(if (status.isPlaying) 1f else 0.98f, label = "ArtworkScale")
    
    Row(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1.2f)
                .pointerInput(Unit) {
                    var totalDrag = 0f
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (abs(totalDrag) > 100) {
                                if (totalDrag > 0) {
                                    viewModel.skipToPrevious()
                                    context.findActivity()?.window?.decorView?.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                } else {
                                    viewModel.skipToNext()
                                    context.findActivity()?.window?.decorView?.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                }
                            }
                            totalDrag = 0f
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            totalDrag += dragAmount
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            if (status.isPlaying) viewModel.pause() else viewModel.resume()
                            context.findActivity()?.window?.decorView?.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                        },
                        onLongPress = {
                            onArtworkFocusToggle()
                            context.findActivity()?.window?.decorView?.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = currentSong.artwork,
                transitionSpec = {
                    fadeIn(tween(400)) togetherWith fadeOut(tween(400))
                },
                label = "ArtworkCrossfade"
            ) { targetArtwork ->
                AsyncImage(
                    model = targetArtwork,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .graphicsLayer(scaleX = artworkScale, scaleY = artworkScale)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .align(Alignment.BottomCenter)
                    .clickable { onToggleVisualizer() }
                    .padding(horizontal = 48.dp, vertical = 24.dp)
            ) {
                when (visualizerMode) {
                    VisualizerMode.SPECTRUM -> SpectrumVisualizer(
                        spectrum = spectrum, 
                        color = accentColor, 
                        modifier = Modifier.fillMaxSize().alpha(0.6f),
                        fpsLimit = deviceState.settings.visualizerFps
                    )
                    VisualizerMode.VU_METER -> VUMeter(magnitude = spectrum.maxOrNull() ?: 0f, color = accentColor, modifier = Modifier.fillMaxSize().alpha(0.6f))
                    VisualizerMode.OFF -> {}
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .padding(48.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Device LED in UI
                        Box(modifier = Modifier.size(8.dp).background(deviceState.audioState.ledColor, CircleShape))
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = currentSong.title.uppercase(),
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            lineHeight = 40.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = currentSong.artist,
                            color = WalkmanOrange,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        QualityBadge(deviceState.verification, onClick = onShowSignalChain)
                    }
                }
                AudioInfoPanel(deviceState)
            }
            
            Spacer(modifier = Modifier.height(64.dp))

            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(formatDuration(status.position), color = MetallicGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(formatDuration(status.duration), color = MetallicGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { status.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = WalkmanOrange,
                    trackColor = Color(0xFF1A1A1A)
                )
            }

            Spacer(modifier = Modifier.height(64.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HardwareButton(
                    icon = MusicHomeIcons.SkipBack, 
                    onClick = { viewModel.skipToPrevious() },
                    onLongPress = {
                        viewModel.seekTo((status.position - 5000).coerceAtLeast(0L))
                    }
                )
                
                HardwareButton(
                    icon = if (status.isPlaying) MusicHomeIcons.Pause else MusicHomeIcons.Play,
                    onClick = { if (status.isPlaying) viewModel.pause() else viewModel.resume() },
                    isPrimary = true
                )

                HardwareButton(
                    icon = MusicHomeIcons.SkipForward, 
                    onClick = { viewModel.skipToNext() },
                    onLongPress = {
                        viewModel.seekTo((status.position + 5000).coerceAtMost(status.duration))
                    }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
            UpNextPreview(deviceState)
        }
    }
}

@Composable
fun PhonePlayerLayout(
    currentSong: Song,
    spectrum: FloatArray,
    deviceState: DeviceState,
    viewModel: MusicViewModel,
    accentColor: Color,
    onShowSignalChain: () -> Unit,
    onArtworkFocusToggle: () -> Unit,
    visualizerMode: VisualizerMode,
    onToggleVisualizer: () -> Unit
) {
    val status = deviceState.playback
    val context = LocalContext.current
    val artworkScale by animateFloatAsState(if (status.isPlaying) 1f else 0.98f, label = "ArtworkScale")
    
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.4f)
                .padding(24.dp)
                .pointerInput(Unit) {
                    var totalDrag = 0f
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (abs(totalDrag) > 100) {
                                if (totalDrag > 0) {
                                    viewModel.skipToPrevious()
                                    context.findActivity()?.window?.decorView?.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                } else {
                                    viewModel.skipToNext()
                                    context.findActivity()?.window?.decorView?.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                }
                            }
                            totalDrag = 0f
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            totalDrag += dragAmount
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            if (status.isPlaying) viewModel.pause() else viewModel.resume()
                            context.findActivity()?.window?.decorView?.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                        },
                        onLongPress = {
                            onArtworkFocusToggle()
                            context.findActivity()?.window?.decorView?.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = currentSong.artwork,
                transitionSpec = {
                    fadeIn(tween(400)) togetherWith fadeOut(tween(400))
                },
                label = "ArtworkCrossfade"
            ) { targetArtwork ->
                AsyncImage(
                    model = targetArtwork,
                    contentDescription = null,
                    modifier = Modifier
                        .aspectRatio(1f)
                        .graphicsLayer(scaleX = artworkScale, scaleY = artworkScale)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f)
                .padding(horizontal = 32.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Device LED in UI
                Box(modifier = Modifier.size(6.dp).background(deviceState.audioState.ledColor, CircleShape))
                Spacer(modifier = Modifier.width(12.dp))
                QualityBadge(deviceState.verification, onClick = onShowSignalChain)
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = currentSong.title.uppercase(),
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                maxLines = 2,
                textAlign = TextAlign.Center,
                lineHeight = 32.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = currentSong.artist,
                color = WalkmanOrange,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clickable { onToggleVisualizer() }
            ) {
                when (visualizerMode) {
                    VisualizerMode.SPECTRUM -> SpectrumVisualizer(
                        spectrum = spectrum, 
                        color = accentColor, 
                        modifier = Modifier.fillMaxSize().alpha(0.8f),
                        fpsLimit = deviceState.settings.visualizerFps
                    )
                    VisualizerMode.VU_METER -> VUMeter(magnitude = spectrum.maxOrNull() ?: 0f, color = accentColor, modifier = Modifier.fillMaxSize().alpha(0.8f))
                    VisualizerMode.OFF -> {}
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                InfoItem(status.format ?: "—")
                InfoItem(if (status.sampleRate != null) "${status.sampleRate / 1000f}k" else "—")
                InfoItem(if (status.bitrate != null) "${status.bitrate / 1000}k" else "—")
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(formatDuration(status.position), color = MetallicGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(formatDuration(status.duration), color = MetallicGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { status.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = WalkmanOrange,
                    trackColor = Color(0xFF1A1A1A)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HardwareButton(
                    icon = MusicHomeIcons.SkipBack, 
                    onClick = { viewModel.skipToPrevious() },
                    onLongPress = { 
                        // Seek backward logic
                        viewModel.seekTo((status.position - 5000).coerceAtLeast(0L))
                    }
                )
                HardwareButton(
                    icon = if (status.isPlaying) MusicHomeIcons.Pause else MusicHomeIcons.Play,
                    onClick = { if (status.isPlaying) viewModel.pause() else viewModel.resume() },
                    isPrimary = true
                )
                HardwareButton(
                    icon = MusicHomeIcons.SkipForward, 
                    onClick = { viewModel.skipToNext() },
                    onLongPress = {
                        // Seek forward logic
                        viewModel.seekTo((status.position + 5000).coerceAtMost(status.duration))
                    }
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun UpNextPreview(state: DeviceState) {
    val currentQueue = state.queue ?: return
    val currentIndex = currentQueue.currentIndex
    val nextSongs = currentQueue.songs.drop(currentIndex + 1).take(3)
    
    if (nextSongs.isEmpty()) return

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "UP NEXT",
            color = MetallicGray,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        nextSongs.forEach { song ->
            Text(
                text = song.title,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 48.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun HardwareButton(
    icon: ImageVector, 
    onClick: () -> Unit, 
    isPrimary: Boolean = false,
    onLongPress: (() -> Unit)? = null
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    
    val baseSize = when {
        screenWidth < 360 -> 52.dp
        screenWidth < 480 -> 60.dp
        screenWidth < 600 -> 68.dp
        else -> 72.dp
    }
    
    val size = if (isPrimary) baseSize * 1.25f else baseSize
    val iconSize = if (isPrimary) 40.dp else 28.dp
    val context = LocalContext.current
    
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.92f else 1f, label = "PressScale")

    Surface(
        modifier = Modifier
            .size(size)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        val pressHaptic = if (isPrimary) HapticFeedbackConstants.KEYBOARD_TAP else HapticFeedbackConstants.VIRTUAL_KEY
                        context.findActivity()?.window?.decorView?.performHapticFeedback(pressHaptic)
                    },
                    onTap = { onClick() },
                    onLongPress = {
                        onLongPress?.invoke()
                    }
                )
            },
        shape = CircleShape,
        color = if (isPrimary) WalkmanOrange else Color(0xFF1A1A1A),
        shadowElevation = if (isPressed) 2.dp else 8.dp,
        border = BorderStroke(1.dp, Color.White.copy(alpha = if (isPressed) 0.2f else 0.1f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            AnimatedContent(
                targetState = icon,
                transitionSpec = {
                    fadeIn(tween(200)) togetherWith fadeOut(tween(200))
                },
                label = "IconTransition"
            ) { targetIcon ->
                Icon(
                    imageVector = targetIcon, 
                    contentDescription = null, 
                    modifier = Modifier.size(iconSize), 
                    tint = if (isPressed) Color.White else Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
fun AudioInfoPanel(state: DeviceState) {
    val status = state.playback
    Column(verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalDivider(modifier = Modifier.width(24.dp), thickness = 1.dp, color = MetallicGray.copy(alpha = 0.3f))
        InfoItem(status.format ?: "—")
        InfoItem(if (status.bitDepth != null) "${status.bitDepth}-BIT" else "—")
        InfoItem(if (status.sampleRate != null) "${status.sampleRate / 1000f} kHz" else "—")
        InfoItem(if (state.output is com.lemonsquad.musichome.ui.models.OutputState.Bluetooth) "BT" else "DAC")
        HorizontalDivider(modifier = Modifier.width(24.dp), thickness = 1.dp, color = MetallicGray.copy(alpha = 0.3f))
    }
}

@Composable
fun InfoItem(label: String) {
    Text(text = label, color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
}

@Composable
fun QualityBadge(verification: VerificationStatus, onClick: () -> Unit) {
    val text = when (verification) {
        VerificationStatus.VERIFIED -> "✓ VERIFIED"
        VerificationStatus.ESTIMATED -> "◉ ESTIMATED"
        VerificationStatus.UNKNOWN -> "? UNKNOWN"
    }
    val color = when (verification) {
        VerificationStatus.VERIFIED -> WalkmanOrange
        VerificationStatus.ESTIMATED -> Color.White
        VerificationStatus.UNKNOWN -> MetallicGray
    }
    Box(
        modifier = Modifier
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text, color = color, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
    }
}

@Composable
fun ArtworkFocusLayout(
    currentSong: Song,
    spectrum: FloatArray,
    deviceState: DeviceState,
    viewModel: MusicViewModel,
    accentColor: Color,
    onExit: () -> Unit
) {
    val status = deviceState.playback
    val context = LocalContext.current
    Box(modifier = Modifier.fillMaxSize().background(PureBlack)) {
        AnimatedContent(
            targetState = currentSong.artwork,
            transitionSpec = { fadeIn(tween(1000)) togetherWith fadeOut(tween(1000)) },
            label = "AmbientArtwork"
        ) { targetArtwork ->
            AsyncImage(model = targetArtwork, contentDescription = null, modifier = Modifier.fillMaxSize().clickable { onExit() }, contentScale = ContentScale.Crop, alpha = 0.6f)
        }
        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colors = listOf(Color.Transparent, PureBlack.copy(alpha = 0.9f)))).clickable { onExit() })
        Column(modifier = Modifier.align(Alignment.BottomCenter).padding(48.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = currentSong.title.uppercase(), color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.Bold, letterSpacing = 4.sp, maxLines = 1)
            Text(text = currentSong.artist, color = WalkmanOrange, fontSize = 24.sp, fontWeight = FontWeight.Medium, letterSpacing = 2.sp)
            Spacer(modifier = Modifier.height(48.dp))
            SpectrumVisualizer(spectrum = spectrum, color = accentColor, modifier = Modifier.fillMaxWidth().height(120.dp).alpha(0.9f))
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "${formatDuration(status.position)} / ${formatDuration(status.duration)}", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(32.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { 
                    context.findActivity()?.window?.decorView?.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    viewModel.skipToPrevious() 
                }) { Icon(MusicHomeIcons.SkipBack, null, Modifier.size(48.dp), Color.White) }
                
                IconButton(onClick = { 
                    context.findActivity()?.window?.decorView?.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    if (status.isPlaying) viewModel.pause() else viewModel.resume() 
                }) {
                    Icon(if (status.isPlaying) MusicHomeIcons.Pause else MusicHomeIcons.Play, null, Modifier.size(72.dp), WalkmanOrange)
                }
                
                IconButton(onClick = { 
                    context.findActivity()?.window?.decorView?.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    viewModel.skipToNext() 
                }) { Icon(MusicHomeIcons.SkipForward, null, Modifier.size(48.dp), Color.White) }
            }
        }
    }
}

fun formatDuration(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
