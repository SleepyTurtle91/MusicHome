package com.lemonsquad.musichome.core.domain.repository

import com.lemonsquad.musichome.core.domain.model.Album
import com.lemonsquad.musichome.core.domain.model.Artist
import com.lemonsquad.musichome.core.domain.model.PlaybackQueue
import com.lemonsquad.musichome.core.domain.model.ScanState
import com.lemonsquad.musichome.core.domain.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface MusicRepository {
    val allSongs: Flow<List<Song>>
    val allAlbums: Flow<List<Album>>
    val allArtists: Flow<List<Artist>>
    val currentQueue: StateFlow<PlaybackQueue?>
    val watchedFolders: Flow<List<String>>
    val scanState: Flow<ScanState>

    fun getSongsByAlbum(albumId: Long): Flow<List<Song>>
    fun getSongsByArtist(artistName: String): Flow<List<Song>>

    fun setQueue(queue: PlaybackQueue)
    fun updateQueueIndex(index: Int)
    fun addManualPath(path: String)
    fun removeManualPath(path: String)

    suspend fun savePlaybackState(songId: Long?, positionMs: Long)
    suspend fun saveLastDestination(destination: String, id: String? = null)
    suspend fun restorePlaybackState(): PlaybackQueue?
    suspend fun getPlaybackPosition(): Long
    suspend fun getLastDestination(): Pair<String, String?>?

    suspend fun syncLibrary()
    suspend fun refreshLibrary()
}
