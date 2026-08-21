package com.lemonsquad.musichome.ui.engine

import android.content.Context
import android.bluetooth.BluetoothA2dp
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.media.AudioManager
import androidx.media3.session.MediaController
import com.lemonsquad.musichome.ui.models.GainStage
import com.lemonsquad.musichome.ui.models.OutputState
import com.lemonsquad.musichome.ui.models.VerificationStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import androidx.media3.common.util.UnstableApi

class Media3AudioEngine(
    private val controller: MediaController,
    private val audioManager: AudioManager,
    private val context: Context,
    private val scope: CoroutineScope
) : AudioController, AudioTelemetry {

    private val _sampleRate = MutableStateFlow<Int?>(null)
    override val sampleRate: Flow<Int?> = _sampleRate.asStateFlow()

    private val _bitDepth = MutableStateFlow<Int?>(null)
    override val bitDepth: Flow<Int?> = _bitDepth.asStateFlow()

    private val _format = MutableStateFlow<String?>(null)
    override val format: Flow<String?> = _format.asStateFlow()

    private val _outputDevice = MutableStateFlow<OutputState>(OutputState.InternalDAC)
    override val outputDevice: Flow<OutputState> = _outputDevice.asStateFlow()

    private val _verificationStatus = MutableStateFlow(VerificationStatus.UNKNOWN)
    override val verificationStatus: Flow<VerificationStatus> = _verificationStatus.asStateFlow()

    init {
        // USB DAC Monitoring
        val filter = IntentFilter().apply {
            addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
            addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        }
        context.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                updateTelemetry()
            }
        }, filter)

        // Start polling for other metrics
        scope.launch {
            while (true) {
                updateTelemetry()
                kotlinx.coroutines.delay(1000)
            }
        }
    }

    private fun updateTelemetry() {
        var rate: Int? = null
        var depth: Int? = null
        var fmt: String? = null

        val tracks = controller.currentTracks
        for (group in tracks.groups) {
            if (group.isSelected) {
                val trackFormat = group.getTrackFormat(0)
                rate = if (trackFormat.sampleRate > 0) trackFormat.sampleRate else null
                fmt = trackFormat.sampleMimeType?.split("/")?.lastOrNull()?.uppercase()
                break
            }
        }

        _sampleRate.value = rate
        _bitDepth.value = depth
        _format.value = fmt

        // L.I.S.A. Architecture Note: Retained deprecated isBluetoothA2dpOn intentionally.
        // Modern heuristic replacements (like getDevices) only prove a device is connected, not that 
        // it is the active route. True High-Honesty telemetry requires AudioTrack.getRoutedDevice(),
        // which Media3 currently obscures. Until we can safely extract the AudioTrack, this remains ESTIMATED.
        @Suppress("DEPRECATION")
        val isBluetooth = audioManager.isBluetoothA2dpOn
        
        // USB DAC Detection
        val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
        val hasUsbDac = usbManager.deviceList.values.any { device ->
            isAudioDevice(device)
        }

        val currentOutput = when {
            isBluetooth -> OutputState.Bluetooth
            hasUsbDac -> OutputState.UsbDAC
            else -> OutputState.InternalDAC
        }
        _outputDevice.value = currentOutput

        _verificationStatus.value = when {
            currentOutput == OutputState.UsbDAC -> VerificationStatus.VERIFIED
            isBluetooth -> VerificationStatus.ESTIMATED // Codec heuristics could go here
            else -> VerificationStatus.UNKNOWN
        }
    }

    private fun isAudioDevice(device: UsbDevice): Boolean {
        for (i in 0 until device.interfaceCount) {
            val iface = device.getInterface(i)
            // USB Class 1: Audio
            if (iface.interfaceClass == 1) return true
        }
        return false
    }

    override fun play() { controller.play() }
    override fun pause() { controller.pause() }
    override fun skipNext() { controller.seekToNext() }
    override fun skipPrevious() { controller.seekToPrevious() }
    override fun seekTo(position: Long) { controller.seekTo(position) }
    override fun setVolume(volume: Int) {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)
    }
    override fun setGain(stage: GainStage) {
        // Implementation for custom hardware would go here
    }
}
