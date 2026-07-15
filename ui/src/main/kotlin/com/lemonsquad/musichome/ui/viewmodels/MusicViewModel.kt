package com.lemonsquad.musichome.ui.viewmodels

import android.content.ComponentName
import android.content.Context
import android.graphics.BitmapFactory
import android.media.AudioManager
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
import com.lemonsquad.musichome.media.player.VisualizerManager
import com.lemonsquad.musichome.ui.models.AlbumPalette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.os.Bundle
import androidx.media3.session.SessionCommand

import androidx.media3.common.util.UnstableApi

class MusicViewModel(
    val repository: MusicRepository,
    private val context: Context
) : ViewModel() {

    private val artworkCache = ArtworkCache(context)
    private val visualizerManager = VisualizerManager() // Note: Service should own the actual instance that is initialized with sessionId
    
    // EQ State
    private val _eqEnabled = MutableStateFlow(true)
    val eqEnabled = _eqEnabled.asStateFlow()

    private val _eqBands = MutableStateFlow(mapOf<Int, Int>())
    val eqBands = _eqBands.asStateFlow()

    // For now, we'll expose the spectrum through the ViewModel if the Service isn't easily accessible for direct UI flow
    // In a production app, the Controller might provide this or we'd bind to the service
    private val _spectrum = MutableStateFlow(FloatArray(16) { 0f })
    val spectrum = _spectrum.asStateFlow()

    private val _playbackStatus = MutableStateFlow(PlaybackStatus())
    val playbackStatus = _playbackStatus.asStateFlow()

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

    private var onDirectoryPickerRequest: (() -> Unit)? = null

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private val controller: MediaController?
        get() = if (controllerFuture?.isDone == true) controllerFuture?.get() else null

    init {
        val sessionToken = SessionToken(context, ComponentName(context, MusicPlaybackService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        
        controllerFuture?.addListener({
            restorePlaybackPosition()
            updatePlaybackStatus()
            checkInitialization()
        }, { it.run() })

        startPlaybackStatusPolling()
        restorePlaybackState()
        syncLibrary()
        
        // Track song changes for Palette
        viewModelScope.launch {
            playbackStatus.map { it.currentSongId }.distinctUntilChanged().collect { id ->
                id?.let { updatePaletteForSong(it) }
            }
        }
    }

    private fun checkInitialization() {
        viewModelScope.launch {
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
    }

    fun resume() {
        controller?.play()
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
        updatePlaybackStatus()
    }

    fun skipToPrevious() {
        controller?.seekToPrevious()
        updatePlaybackStatus()
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
