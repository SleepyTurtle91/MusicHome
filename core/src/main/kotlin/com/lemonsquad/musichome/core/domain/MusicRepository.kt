package com.lemonsquad.musichome.core.domain

import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    fun getAllSongs(): Flow<List<LocalSong>>
    suspend fun syncSongs(songs: List<LocalSong>)
    suspend fun deleteSong(song: LocalSong)
}
