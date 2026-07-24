package com.lemonsquad.musichome.ui.theme

import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.lemonsquad.musichome.ui.icons.MusicHomeIcons

sealed class MusicDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val activeIcon: ImageVector
) {
    data object Library : MusicDestination(
        "library", 
        "Library", 
        MusicHomeIcons.Library, 
        MusicHomeIcons.LibraryActive
    )

    data object Player : MusicDestination(
        "player", 
        "Player", 
        MusicHomeIcons.Player, 
        MusicHomeIcons.PlayerActive
    )

    data object Explore : MusicDestination(
        "explore", 
        "Explore", 
        MusicHomeIcons.Tools, 
        MusicHomeIcons.ToolsActive
    )

    data object Sound : MusicDestination(
        "sound", 
        "Sound", 
        MusicHomeIcons.Sound, 
        MusicHomeIcons.SoundActive
    )

    data object Settings : MusicDestination(
        "settings", 
        "Settings", 
        MusicHomeIcons.Settings, 
        MusicHomeIcons.SettingsActive
    )
    
    companion object {
        val ALL = listOf(Library, Player, Explore, Sound, Settings)
    }
}

enum class AudioState {
    IDLE,
    STANDARD_AUDIO, // 16-bit / 44.1kHz
    HI_RES_AUDIO,    // 24-bit+ or high sample rate
    DSD_AUDIO,       // Direct Stream Digital
    BLUETOOTH,      // Wireless
    SCANNING;       // Busy

    val ledColor: Color
        get() = when (this) {
            IDLE -> Color.Gray.copy(alpha = 0.3f)
            STANDARD_AUDIO -> Color.White
            HI_RES_AUDIO -> WalkmanOrange
            DSD_AUDIO -> Color(0xFFBB86FC) // Purple
            BLUETOOTH -> Color(0xFF2196F3) // Blue
            SCANNING -> WalkmanOrange // Pulsing handled in UI
        }
}
