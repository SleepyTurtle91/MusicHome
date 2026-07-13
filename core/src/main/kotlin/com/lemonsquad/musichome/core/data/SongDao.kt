package com.lemonsquad.musichome.core.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lemonsquad.musichome.core.domain.LocalSong
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Query("SELECT * FROM songs")
    fun getAllSongs(): Flow<List<LocalSong>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(songs: List<LocalSong>)

    @Delete
    suspend fun delete(song: LocalSong)

    @Query("DELETE FROM songs")
    suspend fun deleteAll()
}
