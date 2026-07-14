package com.lemonsquad.musichome.media.player

import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import androidx.room.Room
import com.lemonsquad.musichome.core.data.database.MusicDatabase
import com.lemonsquad.musichome.core.data.media.MediaStoreScanner
import com.lemonsquad.musichome.core.data.repository.LocalMediaRepository
import com.lemonsquad.musichome.core.domain.repository.MusicRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MusicPlaybackService : MediaLibraryService() {

    private var exoPlayer: ExoPlayer? = null
    private var mediaLibrarySession: MediaLibrarySession? = null
    private var equalizerManager: EqualizerManager? = null
    private var visualizerManager: VisualizerManager? = null
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    // Manual DI for now, matches MainActivity
    private lateinit var repository: MusicRepository

    override fun onCreate() {
        super.onCreate()
        
        val database = Room.databaseBuilder(
            applicationContext,
            MusicDatabase::class.java,
            "music_db"
        ).fallbackToDestructiveMigration().build()
        val scanner = MediaStoreScanner(applicationContext)
        repository = LocalMediaRepository(scanner, database.songDao(), applicationContext)

        initializePlayer()
        startPlaybackStateCheckpoint()
    }

    @androidx.annotation.OptIn(UnstableApi::class)
    private fun initializePlayer() {
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

        exoPlayer = ExoPlayer.Builder(this)
            .setAudioAttributes(audioAttributes, true)
            .build()

        exoPlayer?.let { player ->
            equalizerManager = EqualizerManager()
            equalizerManager?.initialize(player.audioSessionId)
            
            visualizerManager = VisualizerManager()
            visualizerManager?.initialize(player.audioSessionId)

            player.addListener(object : Player.Listener {
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    val index = player.currentMediaItemIndex
                    repository.updateQueueIndex(index)
                    savePlaybackState()
                }
            })

            mediaLibrarySession = MediaLibrarySession.Builder(this, player, object : MediaLibrarySession.Callback {})
                .build()
        }
    }

    private fun startPlaybackStateCheckpoint() {
        serviceScope.launch {
            while (true) {
                delay(10000) // Checkpoint every 10 seconds
                savePlaybackState()
            }
        }
    }

    private fun savePlaybackState() {
        exoPlayer?.let { player ->
            val currentMediaItem = player.currentMediaItem
            val currentPosition = player.currentPosition
            
            if (currentMediaItem != null) {
                serviceScope.launch(Dispatchers.IO) {
                    repository.savePlaybackState(
                        songId = currentMediaItem.mediaId.toLongOrNull(),
                        positionMs = currentPosition
                    )
                }
            }
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return mediaLibrarySession
    }

    override fun onDestroy() {
        equalizerManager?.release()
        visualizerManager?.release()
        mediaLibrarySession?.run {
            player.release()
            release()
            mediaLibrarySession = null
        }
        exoPlayer = null
        super.onDestroy()
    }
}
