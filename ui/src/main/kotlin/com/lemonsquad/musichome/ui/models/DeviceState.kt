package com.lemonsquad.musichome.ui.models

import com.lemonsquad.musichome.ui.viewmodels.PlaybackStatus
import com.lemonsquad.musichome.core.domain.model.ScanState
import com.lemonsquad.musichome.ui.theme.AudioState

enum class GainStage {
    LOW, MID, HIGH
}

enum class DeviceMode {
    LISTENING, // Normal active UI
    DESK,      // Standby / Clock mode
    LOCKED,    // Passive screen lock
    DIAGNOSTIC, // Technical view
    HARDWARE_LOCKED // Controls locked to prevent accidental touches
}

enum class StartupResumeMode {
    SILENT, ASK, DISABLED
}

data class DeviceSettings(
    val hapticsEnabled: Boolean = true,
    val pixelShiftingEnabled: Boolean = true,
    val volumeSafetyEnabled: Boolean = true,
    val resumeMode: StartupResumeMode = StartupResumeMode.SILENT,
    val visualizerFps: Int = 30
)

enum class VerificationStatus {
    VERIFIED,  // Hardware confirmed bit-perfect
    ESTIMATED, // Software-level high quality
    UNKNOWN    // No data available
}

data class AudioCapabilities(
    val maxSampleRate: Int = 48000,
    val supportsDsd: Boolean = false,
    val externalDacDetected: Boolean = false
)

data class AudioSession(
    val trackId: String? = null,
    val startedAt: Long = 0,
    val lastPosition: Long = 0,
    val lastTab: String = "library"
)

sealed class OutputState {
    data object InternalDAC : OutputState()
    data object UsbDAC : OutputState()
    data object Bluetooth : OutputState()
    data object Speaker : OutputState()
}

data class NetworkState(
    val isWifiConnected: Boolean = false,
    val signalStrength: Int = 0,
    val isSyncing: Boolean = false
)

data class PowerState(
    val batteryPercent: Int = 0,
    val isCharging: Boolean = false
)

data class DeviceState(
    val playback: PlaybackStatus = PlaybackStatus(),
    val output: OutputState = OutputState.InternalDAC,
    val gain: GainStage = GainStage.MID,
    val mode: DeviceMode = DeviceMode.LISTENING,
    val verification: VerificationStatus = VerificationStatus.UNKNOWN,
    val capabilities: AudioCapabilities = AudioCapabilities(),
    val session: AudioSession = AudioSession(),
    val settings: DeviceSettings = DeviceSettings(),
    val scanState: ScanState = ScanState.Idle,
    val network: NetworkState = NetworkState(),
    val power: PowerState = PowerState()
) {
    val audioState: AudioState
        get() = when {
            output is OutputState.Bluetooth -> AudioState.BLUETOOTH
            playback.format == "DSD" -> AudioState.DSD_AUDIO
            playback.sampleRate != null && (playback.sampleRate > 48000) -> AudioState.HI_RES_AUDIO
            scanState is ScanState.Scanning -> AudioState.SCANNING
            playback.isPlaying -> AudioState.STANDARD_AUDIO
            else -> AudioState.IDLE
        }
}
