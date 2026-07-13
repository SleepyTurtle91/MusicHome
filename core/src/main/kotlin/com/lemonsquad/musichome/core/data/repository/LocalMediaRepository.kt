package com.lemonsquad.musichome.core.data.repository

import android.net.Uri
import com.lemonsquad.musichome.core.data.database.LocalSongEntity
import com.lemonsquad.musichome.core.data.database.SongDao
import com.lemonsquad.musichome.core.data.media.MediaStoreScanner
import com.lemonsquad.musichome.core.domain.model.ScanState
import com.lemonsquad.musichome.core.domain.model.Song
import com.lemonsquad.musichome.core.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class LocalMediaRepository(
    private val scanner: MediaStoreScanner,
    private val dao: SongDao
) : MusicRepository {

    private val _scanState = MutableStateFlow<ScanState>(ScanState.Idle)
    override val scanState: StateFlow<ScanState> = _scanState.asStateFlow()

    override val allSongs: Flow<List<Song>> = dao.getAllSongsSortedByTitle().map { entities ->
        entities.map { it.toDomain() }
    }

    override suspend fun syncLibrary() {
        try {
            _scanState.value = ScanState.Scanning
            val entities = scanner.scan()
            dao.insertAll(entities)
            _scanState.value = ScanState.Finished(entities.size)
        } catch (e: Exception) {
            _scanState.value = ScanState.Error(e.message ?: "Unknown error during scan")
        }
    }

    override suspend fun refreshLibrary() {
        syncLibrary()
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
    path = path
)
