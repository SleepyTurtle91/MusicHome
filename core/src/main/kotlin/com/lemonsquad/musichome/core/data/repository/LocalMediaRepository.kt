package com.lemonsquad.musichome.core.data.repository

import android.net.Uri
import com.lemonsquad.musichome.core.data.database.AlbumDto
import com.lemonsquad.musichome.core.data.database.AppSettingsEntity
import com.lemonsquad.musichome.core.data.database.ArtistDto
import com.lemonsquad.musichome.core.domain.analysis.DuplicateFinder
import com.lemonsquad.musichome.core.domain.analysis.LibraryHealthAnalyzer
import com.lemonsquad.musichome.core.data.database.LibraryStatsEntity
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

    private val duplicateFinder = DuplicateFinder()
    private val healthAnalyzer = LibraryHealthAnalyzer()

    private val _libraryStats = MutableStateFlow(LibraryStats())
    override val libraryStats: StateFlow<LibraryStats> = _libraryStats.asStateFlow()

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

    override val navigationMode: Flow<NavigationMode> = dao.getAppSettings().map { entity ->
        entity?.navigationMode?.let { NavigationMode.valueOf(it) } ?: NavigationMode.AUTO
    }

    override suspend fun syncLibrary() {
        try {
            _scanState.value = ScanState.Indexing(0f)
            
            // Phase 1: Fast Discovery (MediaStore)
            val mediaStoreSongs = scanner.scan()
            
            // Phase 2: Delta Comparison
            val currentSongs = dao.getAllSongsSortedByTitle().first().associateBy { it.id }
            val songsToInsert = mutableListOf<LocalSongEntity>()
            
            mediaStoreSongs.forEachIndexed { index, newSong ->
                val existing = currentSongs[newSong.id]
                if (existing == null || existing.dateModified != newSong.dateModified || existing.size != newSong.size) {
                    songsToInsert.add(newSong)
                }
                
                if (index % 100 == 0) {
                    _scanState.value = ScanState.Indexing(index.toFloat() / mediaStoreSongs.size)
                }
            }

            if (songsToInsert.isNotEmpty()) {
                _scanState.value = ScanState.Enriching(0, songsToInsert.size)
                // Batch insert
                dao.insertAll(songsToInsert)
            }

            // Phase 3: Analytics (Async)
            repositoryScope.launch(Dispatchers.Default) {
                updateLibraryStats()
            }

            _scanState.value = ScanState.Finished(mediaStoreSongs.size)
        } catch (e: Exception) {
            _scanState.value = ScanState.Error(e.message ?: "Unknown error during scan")
        }
    }

    private suspend fun updateLibraryStats() {
        val songs = allSongs.first()
        if (songs.isEmpty()) return
        
        val duplicates = duplicateFinder.findDuplicates(songs).sumOf { it.songs.size - 1 }
        val stats = healthAnalyzer.analyze(songs, duplicates)
        
        _libraryStats.value = stats
        
        val entity = LibraryStatsEntity(
            totalSongs = stats.totalSongs,
            totalAlbums = stats.totalAlbums,
            totalArtists = stats.totalArtists,
            missingArtworkCount = stats.missingArtworkCount,
            duplicateCount = stats.duplicateCount,
            healthScore = stats.healthScore,
            lastScanTimestamp = stats.lastScanTimestamp
        )
        dao.saveLibraryStats(entity)
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

    override fun updateQueueOrder(songs: List<Song>) {
        _currentQueue.value = _currentQueue.value?.let { 
            it.copy(songs = songs, revision = it.revision + 1)
        }
    }

    override fun setShuffleEnabled(enabled: Boolean) {
        _currentQueue.value = _currentQueue.value?.copy(shuffleEnabled = enabled)
    }

    override fun setRepeatMode(mode: RepeatMode) {
        _currentQueue.value = _currentQueue.value?.copy(repeatMode = mode)
    }

    override fun setNavigationMode(mode: NavigationMode) {
        repositoryScope.launch(Dispatchers.IO) {
            dao.saveAppSettings(AppSettingsEntity(navigationMode = mode.name))
        }
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
            repeatMode = queue.repeatMode.ordinal,
            shuffleEnabled = queue.shuffleEnabled,
            queueRevision = queue.revision,
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
            sourceName = state.sourceName,
            repeatMode = RepeatMode.values().getOrElse(state.repeatMode) { RepeatMode.NONE },
            shuffleEnabled = state.shuffleEnabled,
            revision = state.queueRevision
        )
    }

    override suspend fun getPlaybackPosition(): Long {
        return dao.getPlaybackState()?.positionMs ?: 0L
    }

    override suspend fun getLastDestination(): Pair<String, String?>? {
        val state = dao.getPlaybackState() ?: return null
        return state.lastDestination?.let { it to state.lastDestinationId }
    }

    override suspend fun updateSongMetadata(id: Long, title: String, artist: String, album: String?) {
        dao.updateSongMetadata(id, title, artist, album)
    }

    override suspend fun deleteSong(id: Long) {
        dao.deleteSong(id)
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
    mimeType = mimeType,
    trackNumber = trackNumber,
    size = size,
    dateModified = dateModified,
    replayGain = replayGain,
    albumGain = albumGain,
    replayPeak = replayPeak,
    loudnessRange = loudnessRange
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
