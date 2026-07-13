package com.lemonsquad.musichome.core.domain.repository

import com.lemonsquad.musichome.core.domain.model.ScanState
import com.lemonsquad.musichome.core.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    val allSongs: Flow<List<Song>>
    val scanState: Flow<ScanState>

    suspend fun syncLibrary()
    suspend fun refreshLibrary()
}
