package com.lemonsquad.musichome.core.data.repository

import android.net.Uri
import com.lemonsquad.musichome.core.data.database.AlbumDto
import com.lemonsquad.musichome.core.data.database.ArtistDto
import com.lemonsquad.musichome.core.data.database.LocalSongEntity
import com.lemonsquad.musichome.core.data.database.PlaybackStateEntity
import com.lemonsquad.musichome.core.data.database.SongDao
import com.lemonsquad.musichome.core.data.database.WatchedFolderEntity
import com.lemonsquad.musichome.core.data.media.ManualScanner
import com.lemonsquad.musichome.core.data.media.MediaStoreObserver
import com.lemonsquad.musichome.core.data.media.MediaStoreScanner
import com.lemonsquad.musichome.core.domain.model.*
import com.lemonsquad.musichome.core.domain.repository.MusicRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.launch

class LocalMediaRepository(
    private val scanner: MediaStoreScanner,
    private val dao: SongDao,
    context: android.content.Context
) : MusicRepository {

    private val manualScanner = ManualScanner(context)
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val observer = MediaStoreObserver(context) {
        syncLibrary()
    }

    init {
        observer.register()
    }

    private val _scanState = MutableStateFlow<ScanState>(ScanState.Idle)
    override val scanState: StateFlow<ScanState> = _scanState.asStateFlow()

    private val _currentQueue = MutableStateFlow<PlaybackQueue?>(null)
    override val currentQueue: StateFlow<PlaybackQueue?> = _currentQueue.asStateFlow()

    override val watchedFolders: Flow<List<String>> = dao.getAllWatchedFolders().map { entities ->
        entities.map { it.path }
    }

    override val allSongs: Flow<List<Song>> = dao.getAllSongsSortedByTitle().map { entities ->
        entities.map { it.toDomain() }
    }

    override val allAlbums: Flow<List<Album>> = dao.getAlbums().map { dtos ->
        dtos.map { it.toDomain() }
    }

    override val allArtists: Flow<List<Artist>> = dao.getArtists().map { dtos ->
        dtos.map { it.toDomain() }
    }

    override suspend fun syncLibrary() {
        try {
            _scanState.value = ScanState.Scanning
            val mediaStoreSongs = scanner.scan()
            
            val manualSongs = mutableListOf<LocalSongEntity>()
            val folders = dao.getAllWatchedFolders().first()
            for (folder in folders) {
                manualSongs.addAll(manualScanner.scanFolder(folder.path))
            }

            dao.insertAll(mediaStoreSongs + manualSongs)
            _scanState.value = ScanState.Finished(mediaStoreSongs.size + manualSongs.size)
        } catch (e: Exception) {
            _scanState.value = ScanState.Error(e.message ?: "Unknown error during scan")
        }
    }

    override suspend fun refreshLibrary() {
        syncLibrary()
    }

    override fun getSongsByAlbum(albumId: Long): Flow<List<Song>> {
        return dao.getSongsByAlbum(albumId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getSongsByArtist(artistName: String): Flow<List<Song>> {
        return dao.getSongsByArtist(artistName).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun setQueue(queue: PlaybackQueue) {
        _currentQueue.value = queue
    }

    override fun updateQueueIndex(index: Int) {
        _currentQueue.value = _currentQueue.value?.copy(currentIndex = index)
    }

    override fun addManualPath(path: String) {
        repositoryScope.launch {
            dao.insertWatchedFolder(WatchedFolderEntity(path))
            syncLibrary()
        }
    }

    override fun removeManualPath(path: String) {
        repositoryScope.launch {
            dao.deleteWatchedFolder(path)
            // Ideally also cleanup songs from DB that are in this path and NOT in MediaStore
            syncLibrary()
        }
    }

    override suspend fun savePlaybackState(songId: Long?, positionMs: Long) {
        val queue = _currentQueue.value ?: return
        val existing = dao.getPlaybackState()
        val state = PlaybackStateEntity(
            lastSongId = songId,
            positionMs = positionMs,
            queueIds = queue.songs.joinToString(",") { it.id.toString() },
            queueIndex = queue.currentIndex,
            source = queue.source.name,
            sourceName = queue.sourceName,
            lastDestination = existing?.lastDestination,
            lastDestinationId = existing?.lastDestinationId,
            updatedAt = System.currentTimeMillis()
        )
        dao.savePlaybackState(state)
    }

    override suspend fun saveLastDestination(destination: String, id: String?) {
        val existing = dao.getPlaybackState()
        val state = existing?.copy(
            lastDestination = destination,
            lastDestinationId = id,
            updatedAt = System.currentTimeMillis()
        ) ?: PlaybackStateEntity(
            lastSongId = null,
            positionMs = 0,
            queueIds = "",
            queueIndex = 0,
            source = null,
            sourceName = null,
            lastDestination = destination,
            lastDestinationId = id,
            updatedAt = System.currentTimeMillis()
        )
        dao.savePlaybackState(state)
    }

    override suspend fun restorePlaybackState(): PlaybackQueue? {
        val state = dao.getPlaybackState() ?: return null
        if (state.queueIds.isEmpty()) return null
        val ids = state.queueIds.split(",").mapNotNull { it.toLongOrNull() }
        if (ids.isEmpty()) return null
        
        val entities = dao.getSongsByIds(ids)
        // Reorder songs based on original queueIds
        val songMap = entities.associateBy { it.id }
        val songs = ids.mapNotNull { songMap[it]?.toDomain() }
        
        return PlaybackQueue(
            songs = songs,
            currentIndex = state.queueIndex,
            source = state.source?.let { QueueSource.valueOf(it) } ?: QueueSource.ALL_SONGS,
            sourceName = state.sourceName
        )
    }

    override suspend fun getPlaybackPosition(): Long {
        return dao.getPlaybackState()?.positionMs ?: 0L
    }

    override suspend fun getLastDestination(): Pair<String, String?>? {
        val state = dao.getPlaybackState() ?: return null
        return state.lastDestination?.let { it to state.lastDestinationId }
    }
}

private fun LocalSongEntity.toDomain(): Song = Song(
    id = id,
    title = title,
    artist = artist,
    album = album,
    duration = duration,
    artwork = albumArtUri?.let { Uri.parse(it) },
    mediaUri = Uri.parse(path), // Simplified for local path
    path = path,
    trackNumber = trackNumber
)

private fun AlbumDto.toDomain(): Album = Album(
    id = id,
    title = title,
    artist = artist,
    artworkUri = artworkUri?.let { Uri.parse(it) },
    songCount = songCount
)

private fun ArtistDto.toDomain(): Artist = Artist(
    name = name,
    albumCount = albumCount,
    songCount = songCount,
    artworkUri = artworkUri?.let { Uri.parse(it) }
)
