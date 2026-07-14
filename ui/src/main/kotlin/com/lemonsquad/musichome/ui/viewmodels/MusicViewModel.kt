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
import com.lemonsquad.musichome.core.data.media.ArtworkCache
import com.lemonsquad.musichome.core.domain.model.*
import com.lemonsquad.musichome.core.domain.repository.MusicRepository
import com.lemonsquad.musichome.media.player.MusicPlaybackService
import com.lemonsquad.musichome.media.player.VisualizerManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MusicViewModel(
    val repository: MusicRepository,
    private val context: Context
) : ViewModel() {

    private val artworkCache = ArtworkCache(context)
    private val visualizerManager = VisualizerManager() // Note: Service should own the actual instance that is initialized with sessionId
    
    // For now, we'll expose the spectrum through the ViewModel if the Service isn't easily accessible for direct UI flow
    // In a production app, the Controller might provide this or we'd bind to the service
    private val _spectrum = MutableStateFlow(FloatArray(16) { 0f })
    val spectrum = _spectrum.asStateFlow()

    private val _playbackStatus = MutableStateFlow(PlaybackStatus())
    val playbackStatus = _playbackStatus.asStateFlow()

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
            updatePlaybackStatus()
        }, { it.run() })

        startPlaybackStatusPolling()
        restorePlaybackState()
        syncLibrary()
    }

    private fun startPlaybackStatusPolling() {
        viewModelScope.launch {
            while (true) {
                updatePlaybackStatus()
                delay(1000) // Update every second
            }
        }
    }

    private fun updatePlaybackStatus() {
        controller?.let {
            _playbackStatus.value = PlaybackStatus(
                position = it.currentPosition,
                duration = it.duration.coerceAtLeast(0L),
                isPlaying = it.isPlaying,
                currentSongId = it.currentMediaItem?.mediaId
            )
        }
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

    fun skipToNext() {
        controller?.seekToNext()
        updatePlaybackStatus()
    }

    fun skipToPrevious() {
        controller?.seekToPrevious()
        updatePlaybackStatus()
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
