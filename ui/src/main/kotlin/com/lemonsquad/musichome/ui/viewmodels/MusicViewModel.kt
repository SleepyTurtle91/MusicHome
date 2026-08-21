package com.lemonsquad.musichome.ui.viewmodels

import android.content.ComponentName
import android.content.Context
import android.graphics.BitmapFactory
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.palette.graphics.Palette
import androidx.media3.common.C
import com.google.common.util.concurrent.ListenableFuture
import androidx.compose.ui.graphics.Color
import com.lemonsquad.musichome.core.data.media.ArtworkCache
import com.lemonsquad.musichome.core.domain.model.*
import com.lemonsquad.musichome.core.domain.repository.MusicRepository
import com.lemonsquad.musichome.media.player.MusicPlaybackService
import com.lemonsquad.musichome.ui.models.AlbumPalette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.os.Bundle
import androidx.media3.session.SessionCommand
import com.lemonsquad.musichome.ui.engine.AudioVisualizer
import com.lemonsquad.musichome.ui.engine.Media3AudioEngine
import com.lemonsquad.musichome.ui.engine.*
import com.lemonsquad.musichome.ui.models.*
import com.lemonsquad.musichome.ui.models.DeviceMode
import com.lemonsquad.musichome.ui.models.AudioSession
import com.lemonsquad.musichome.ui.models.AudioCapabilities
import androidx.media3.common.util.UnstableApi

class MusicViewModel(
    val repository: MusicRepository,
    private val context: Context
) : ViewModel(), AudioVisualizer {

    private val artworkCache = ArtworkCache(context)
    
    // Audio Engine Components
    @androidx.annotation.OptIn(UnstableApi::class)
    private var audioEngine: Media3AudioEngine? = null
    
    // AudioVisualizer implementation
    private val _spectrum = MutableStateFlow(FloatArray(16))
    override val spectrum: Flow<FloatArray> = _spectrum.asStateFlow()
    
    private val _vuLevel = MutableStateFlow(0f)
    override val vuLevel: Flow<Float> = _vuLevel.asStateFlow()

    // EQ State
    private val _eqEnabled = MutableStateFlow(true)
    val eqEnabled = _eqEnabled.asStateFlow()

    private val _eqBands = MutableStateFlow(mapOf<Int, Int>())
    val eqBands = _eqBands.asStateFlow()

    private val _playbackStatus = MutableStateFlow(PlaybackStatus())
    val playbackStatus = _playbackStatus.asStateFlow()

    private val _gainStage = MutableStateFlow(GainStage.MID)
    private val _deviceMode = MutableStateFlow(DeviceMode.LISTENING)
    private val _deviceSettings = MutableStateFlow(DeviceSettings())
    private val _networkState = MutableStateFlow(NetworkState())
    private val _powerState = MutableStateFlow(PowerState())
    private val _audioCapabilities = MutableStateFlow(AudioCapabilities())
    private val _audioSession = MutableStateFlow(AudioSession())

    @androidx.annotation.OptIn(UnstableApi::class)
    val deviceState: StateFlow<DeviceState> = combine(
        playbackStatus,
        repository.currentQueue,
        repository.scanState,
        _gainStage,
        _deviceMode,
        _networkState,
        _powerState,
        _audioCapabilities,
        _audioSession,
        _deviceSettings
    ) { flows ->
        val playback = flows[0] as PlaybackStatus
        val queue = flows[1] as? PlaybackQueue
        val scan = flows[2] as ScanState
        val gain = flows[3] as GainStage
        val mode = flows[4] as DeviceMode
        val network = flows[5] as NetworkState
        val power = flows[6] as PowerState
        val caps = flows[7] as AudioCapabilities
        val session = flows[8] as AudioSession
        val settings = flows[9] as DeviceSettings

        // Telemetry driven logic
        // L.I.S.A. Architecture Note: Retained deprecated isBluetoothA2dpOn intentionally.
        // Modern heuristic replacements (like getDevices) only prove a device is connected, not that 
        // it is the active route. True High-Honesty telemetry requires AudioTrack.getRoutedDevice(),
        // which Media3 currently obscures. Until we can safely extract the AudioTrack, this remains ESTIMATED.
        @Suppress("DEPRECATION")
        val isBluetooth = audioManager.isBluetoothA2dpOn

        val hasUsbAudio = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS).any { device ->
            device.type == AudioDeviceInfo.TYPE_USB_DEVICE ||
            device.type == AudioDeviceInfo.TYPE_USB_HEADSET ||
            device.type == AudioDeviceInfo.TYPE_USB_ACCESSORY
        }

        val output = when {
            isBluetooth -> OutputState.Bluetooth
            hasUsbAudio -> OutputState.UsbDAC
            else -> OutputState.InternalDAC
        }

        val verification = when {
            output == OutputState.UsbDAC -> VerificationStatus.VERIFIED
            output == OutputState.Bluetooth -> VerificationStatus.ESTIMATED
            playback.isHighRes -> VerificationStatus.ESTIMATED
            else -> VerificationStatus.UNKNOWN
        }

        DeviceState(
            playback = playback,
            queue = queue,
            output = output,
            gain = gain,
            mode = mode,
            verification = verification,
            capabilities = caps,
            session = session,
            settings = settings,
            scanState = scan,
            network = network,
            power = power
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DeviceState()
    )

    private val _currentPalette = MutableStateFlow(AlbumPalette())
    val currentPalette = _currentPalette.asStateFlow()

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized = _isInitialized.asStateFlow()

    val navigationMode: StateFlow<NavigationMode> = repository.navigationMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NavigationMode.AUTO
        )

    val uiState: StateFlow<MusicUiState> = combine(
        repository.allSongs,
        repository.allAlbums,
        repository.allArtists
    ) { songs, albums, artists ->
        if (songs.isEmpty() && albums.isEmpty() && artists.isEmpty()) {
            MusicUiState.Empty
        } else {
            MusicUiState.Success(songs, albums, artists)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MusicUiState.Loading
    )

    private val _albumDetailState = MutableStateFlow<AlbumDetailUiState>(AlbumDetailUiState.Loading)
    val albumDetailState: StateFlow<AlbumDetailUiState> = _albumDetailState.asStateFlow()

    val scanState = repository.scanState

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val _volumeLevel = MutableStateFlow(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC))
    val volumeLevel = _volumeLevel.asStateFlow()
    
    private val _showVolumeHud = MutableStateFlow(false)
    val showVolumeHud = _showVolumeHud.asStateFlow()

    private val prefs = context.getSharedPreferences("music_home_appearance_prefs", Context.MODE_PRIVATE)
    
    private val _accentColor = MutableStateFlow(prefs.getInt("accent_color", 0xFFFF6A00.toInt()))
    val accentColor: StateFlow<Int> = _accentColor.asStateFlow()

    private val _trueBlack = MutableStateFlow(prefs.getBoolean("true_black", true))
    val trueBlack: StateFlow<Boolean> = _trueBlack.asStateFlow()

    fun setAccentColor(colorValue: Int) {
        _accentColor.value = colorValue
        prefs.edit().putInt("accent_color", colorValue).apply()
    }

    fun setTrueBlack(enabled: Boolean) {
        _trueBlack.value = enabled
        prefs.edit().putBoolean("true_black", enabled).apply()
    }

    private var onDirectoryPickerRequest: (() -> Unit)? = null

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private val controller: MediaController?
        get() = if (controllerFuture?.isDone == true) controllerFuture?.get() else null

    init {
        val sessionToken = SessionToken(context, ComponentName(context, MusicPlaybackService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        
        controllerFuture?.addListener({
            controller?.let { mc ->
                audioEngine = Media3AudioEngine(mc, audioManager, context, viewModelScope)
            }
            restorePlaybackPosition()
            updatePlaybackStatus()
            checkInitialization()
        }, { it.run() })

        startPlaybackStatusPolling()
        startDeviceMetricsPolling()
        restorePlaybackState()
        syncLibrary()
        
        // Track session changes
        viewModelScope.launch {
            combine(playbackStatus, _deviceMode) { pb, mode -> pb to mode }.collect { (pb, mode) ->
                _audioSession.value = AudioSession(
                    trackId = pb.currentSongId,
                    lastPosition = pb.position,
                    lastTab = "player" // Simple tracking for now
                )
            }
        }
    }

    private fun checkInitialization() {
        viewModelScope.launch {
            // Volume Safety Limit
            if (_deviceSettings.value.volumeSafetyEnabled) {
                val currentVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                val maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                if (currentVol.toFloat() / maxVol > 0.7f) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (maxVol * 0.5f).toInt(), 0)
                }
            }
            // Minimum boot time 700ms
            delay(700)
            _isInitialized.value = true
        }
    }

    private fun restorePlaybackPosition() {
        viewModelScope.launch {
            val position = repository.getPlaybackPosition()
            if (position > 0) {
                controller?.seekTo(position)
            }
        }
    }

    private fun startPlaybackStatusPolling() {
        viewModelScope.launch {
            while (true) {
                updatePlaybackStatus()
                delay(1000) // Update every second
            }
        }
    }

    private fun startDeviceMetricsPolling() {
        viewModelScope.launch {
            while (true) {
                updateDeviceMetrics()
                delay(10000) // Every 10s
            }
        }
    }

    private fun updateDeviceMetrics() {
        // Battery
        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val batteryPercent = if (level >= 0 && scale > 0) (level * 100 / scale) else 0
        val isCharging = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) == BatteryManager.BATTERY_STATUS_CHARGING
        _powerState.value = PowerState(batteryPercent, isCharging)

        // Network
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetwork
        val caps = cm.getNetworkCapabilities(activeNetwork)
        val isWifi = caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
        _networkState.value = NetworkState(isWifiConnected = isWifi)
    }

    @androidx.annotation.OptIn(UnstableApi::class)
    private fun updatePlaybackStatus() {
        controller?.let { player ->
            var bitrate: Int? = null
            var sampleRate: Int? = null
            var format: String? = null
            var channels: Int? = null

            val tracks = player.currentTracks
            for (group in tracks.groups) {
                if (group.isSelected) {
                    val trackFormat = group.getTrackFormat(0)
                    bitrate = if (trackFormat.bitrate > 0) trackFormat.bitrate else null
                    sampleRate = if (trackFormat.sampleRate > 0) trackFormat.sampleRate else null
                    format = trackFormat.sampleMimeType?.split("/")?.lastOrNull()?.uppercase()
                    channels = if (trackFormat.channelCount > 0) trackFormat.channelCount else null
                    break
                }
            }

            _playbackStatus.value = PlaybackStatus(
                position = player.currentPosition,
                duration = player.duration.coerceAtLeast(0L),
                isPlaying = player.isPlaying,
                currentSongId = player.currentMediaItem?.mediaId,
                bitrate = bitrate,
                sampleRate = sampleRate,
                format = format,
                channels = channels
            )
        }
    }

    private suspend fun updatePaletteForSong(songId: String) {
        val song = (uiState.value as? MusicUiState.Success)?.songs?.find { it.id.toString() == songId }
        song?.artwork?.let { uri ->
            _currentPalette.value = extractPalette(uri)
        }
    }

    private suspend fun extractPalette(uri: android.net.Uri): AlbumPalette = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            if (bitmap != null) {
                val p = Palette.from(bitmap).generate()
                return@withContext AlbumPalette(
                    dominant = Color(p.getDominantColor(0xFF000000.toInt())),
                    darkVibrant = Color(p.getDarkVibrantColor(0xFF1A1A1A.toInt())),
                    muted = Color(p.getMutedColor(0xFF424242.toInt())),
                    lightVibrant = Color(p.getLightVibrantColor(0xFFBDBDBD.toInt()))
                )
            }
        } catch (e: Exception) {
            // error
        }
        AlbumPalette()
    }

    private fun restorePlaybackState() {
        viewModelScope.launch {
            val restoredQueue = repository.restorePlaybackState()
            if (restoredQueue != null) {
                repository.setQueue(restoredQueue)
                // We'll let the user press play or wait for the controller to be ready
                // Actually, if we have a controller, we can set the items now
                // but usually restoration happens before controller is fully ready.
            }
        }
    }

    fun syncLibrary() {
        viewModelScope.launch {
            repository.syncLibrary()
        }
    }

    fun playSong(song: Song, contextList: List<Song> = listOf(song)) {
        val index = contextList.indexOf(song).coerceAtLeast(0)
        val queue = PlaybackQueue(
            songs = contextList,
            currentIndex = index,
            source = QueueSource.ALL_SONGS
        )
        playQueue(queue)
    }

    fun playQueue(queue: PlaybackQueue) {
        repository.setQueue(queue)
        val mediaItems = queue.songs.map { song ->
            MediaItem.Builder()
                .setMediaId(song.id.toString())
                .setUri(song.mediaUri)
                .build()
        }
        
        controller?.apply {
            setMediaItems(mediaItems, queue.currentIndex, C.TIME_UNSET)
            prepare()
            play()
        }
    }

    fun playAlbum(album: Album, songs: List<Song>, startIndex: Int = 0) {
        val queue = PlaybackQueue(
            songs = songs,
            currentIndex = startIndex,
            source = QueueSource.ALBUM,
            sourceName = album.title
        )
        playQueue(queue)
    }

    fun shuffleAlbum(album: Album, songs: List<Song>) {
        val shuffledSongs = songs.shuffled()
        val queue = PlaybackQueue(
            songs = shuffledSongs,
            currentIndex = 0,
            source = QueueSource.ALBUM,
            sourceName = album.title
        )
        playQueue(queue)
    }

    fun pause() {
        controller?.pause()
        audioEngine?.pause()
        updatePlaybackStatus()
    }

    fun resume() {
        controller?.play()
        audioEngine?.play()
        updatePlaybackStatus()
    }

    fun updateQueueIndex(index: Int) {
        repository.updateQueueIndex(index)
        controller?.seekToDefaultPosition(index)
    }

    fun setEqEnabled(enabled: Boolean) {
        _eqEnabled.value = enabled
        controller?.sendCustomCommand(
            SessionCommand(MusicPlaybackService.COMMAND_SET_EQ_ENABLED, Bundle.EMPTY),
            Bundle().apply { putBoolean(MusicPlaybackService.EXTRA_ENABLED, enabled) }
        )
    }

    fun setEqBandLevel(bandIndex: Int, level: Int) {
        val newBands = _eqBands.value.toMutableMap()
        newBands[bandIndex] = level
        _eqBands.value = newBands
        
        controller?.sendCustomCommand(
            SessionCommand(MusicPlaybackService.COMMAND_SET_EQ_BAND, Bundle.EMPTY),
            Bundle().apply {
                putInt(MusicPlaybackService.EXTRA_BAND_INDEX, bandIndex)
                putInt(MusicPlaybackService.EXTRA_BAND_LEVEL, level)
            }
        )
    }

    fun applyEqPreset(name: String) {
        val levels = EqualizerSettings.PRESETS[name] ?: return
        levels.forEachIndexed { index, level ->
            setEqBandLevel(index, level)
        }
    }

    fun setSleepTimer(minutes: Int) {
        controller?.sendCustomCommand(
            SessionCommand(MusicPlaybackService.COMMAND_SET_SLEEP_TIMER, Bundle.EMPTY),
            Bundle().apply {
                putLong(MusicPlaybackService.EXTRA_DURATION_MS, minutes * 60 * 1000L)
            }
        )
    }

    fun skipToNext() {
        controller?.seekToNext()
        audioEngine?.skipNext()
        updatePlaybackStatus()
    }

    fun skipToPrevious() {
        controller?.seekToPrevious()
        audioEngine?.skipPrevious()
        updatePlaybackStatus()
    }

    fun moveQueueItem(from: Int, to: Int) {
        val current = _playbackStatus.value.currentSongId
        val queue = repository.currentQueue.value ?: return
        val list = queue.songs.toMutableList()
        val item = list.removeAt(from)
        list.add(to, item)
        
        val newIndex = list.indexOfFirst { it.id.toString() == current }
        repository.updateQueueOrder(list)
        if (newIndex != -1) {
            repository.updateQueueIndex(newIndex)
        }
        
        viewModelScope.launch {
            repository.savePlaybackState(
                songId = current?.toLongOrNull(),
                positionMs = _playbackStatus.value.position
            )
        }
    }

    fun removeQueueItem(index: Int) {
        val current = _playbackStatus.value.currentSongId
        val queue = repository.currentQueue.value ?: return
        if (index == queue.currentIndex) return // Don't remove currently playing for now
        
        val list = queue.songs.toMutableList()
        list.removeAt(index)
        
        val newIndex = list.indexOfFirst { it.id.toString() == current }
        repository.updateQueueOrder(list)
        if (newIndex != -1) {
            repository.updateQueueIndex(newIndex)
        }

        viewModelScope.launch {
            repository.savePlaybackState(
                songId = current?.toLongOrNull(),
                positionMs = _playbackStatus.value.position
            )
        }
    }

    fun setShuffleEnabled(enabled: Boolean) {
        repository.setShuffleEnabled(enabled)
        saveSession()
    }

    fun setRepeatMode(mode: RepeatMode) {
        repository.setRepeatMode(mode)
        saveSession()
    }

    private fun saveSession() {
        viewModelScope.launch {
            repository.savePlaybackState(
                songId = _playbackStatus.value.currentSongId?.toLongOrNull(),
                positionMs = _playbackStatus.value.position
            )
        }
    }

    fun toggleNavigationMode() {
        val current = navigationMode.value
        val next = when (current) {
            NavigationMode.AUTO -> NavigationMode.EXPANDED
            NavigationMode.EXPANDED -> NavigationMode.COMPACT
            NavigationMode.COMPACT -> NavigationMode.AUTO
        }
        repository.setNavigationMode(next)
    }

    fun adjustVolume(delta: Int) {
        val current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val next = (current + delta).coerceIn(0, max)
        
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, next, 0)
        _volumeLevel.value = next
        
        viewModelScope.launch {
            _showVolumeHud.value = true
            delay(2000)
            _showVolumeHud.value = false
        }
    }

    fun seekTo(position: Long) {
        controller?.seekTo(position)
        audioEngine?.seekTo(position)
    }

    fun setGainStage(stage: GainStage) {
        _gainStage.value = stage
    }

    fun toggleDeviceMode(mode: DeviceMode) {
        _deviceMode.value = mode
    }

    fun updateSettings(settings: DeviceSettings) {
        _deviceSettings.value = settings
    }

    fun selectAlbum(album: Album) {
        viewModelScope.launch {
            _albumDetailState.value = AlbumDetailUiState.Loading
            repository.getSongsByAlbum(album.id).collect { songs ->
                val artworkUri = artworkCache.getArtwork(album.id, album.artist)
                val dominantColor = artworkUri?.let { extractDominantColor(it, context) }
                _albumDetailState.value = AlbumDetailUiState.Success(
                    album = album,
                    songs = songs,
                    artworkUri = artworkUri,
                    dominantColor = dominantColor
                )
            }
        }
    }

    fun setDirectoryPicker(request: () -> Unit) {
        onDirectoryPickerRequest = request
    }

    fun saveLastDestination(route: String, id: String? = null) {
        viewModelScope.launch {
            repository.saveLastDestination(route, id)
        }
    }

    suspend fun getLastDestination(): Pair<String, String?>? {
        return repository.getLastDestination()
    }

    fun requestDirectoryPicker() {
        onDirectoryPickerRequest?.invoke()
    }

    private suspend fun extractDominantColor(uri: android.net.Uri, context: Context): Int? = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            if (bitmap != null) {
                val palette = Palette.from(bitmap).generate()
                return@withContext palette.getDominantColor(0)
            }
        } catch (e: Exception) {
            // Error extracting color
        }
        null
    }

    override fun onCleared() {
        super.onCleared()
        controllerFuture?.let {
            MediaController.releaseFuture(it)
        }
    }
}
