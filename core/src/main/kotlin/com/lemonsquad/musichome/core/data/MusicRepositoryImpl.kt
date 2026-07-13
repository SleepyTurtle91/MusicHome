package com.lemonsquad.musichome.core.data

import com.lemonsquad.musichome.core.domain.LocalSong
import com.lemonsquad.musichome.core.domain.MusicRepository
import kotlinx.coroutines.flow.Flow

class MusicRepositoryImpl(
    private val songDao: SongDao
) : MusicRepository {

    override fun getAllSongs(): Flow<List<LocalSong>> {
        return songDao.getAllSongs()
    }

    override suspend fun syncSongs(songs: List<LocalSong>) {
        songDao.insertAll(songs)
    }

    override suspend fun deleteSong(song: LocalSong) {
        songDao.delete(song)
    }
}
