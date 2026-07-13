package com.lemonsquad.musichome.core.repository

import com.lemonsquad.musichome.core.data.database.LocalSongEntity
import com.lemonsquad.musichome.core.data.database.SongDao
import com.lemonsquad.musichome.core.data.scanner.MediaStoreScanner
import kotlinx.coroutines.flow.Flow

class LocalMediaRepository(
    private val songDao: SongDao,
    private val mediaStoreScanner: MediaStoreScanner
) {

    /**
     * Reactive stream of the local database. 
     * The :ui module will eventually collect this Flow.
     */
    val allSongs: Flow<List<LocalSongEntity>> = songDao.getAllSongsSortedByTitle()

    /**
     * Triggers a scan of the device and updates the Room database.
     * Because the Dao uses REPLACE on conflict, this safely updates existing records
     * and inserts new ones without duplicating data.
     */
    suspend fun syncDeviceMedia() {
        val scannedSongs = mediaStoreScanner.scanLocalMedia()
        
        if (scannedSongs.isNotEmpty()) {
            songDao.insertAll(scannedSongs)
        }
    }
}
