package com.lemonsquad.musichome.core.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(songs: List<LocalSongEntity>)

    @Query("SELECT * FROM local_songs ORDER BY title ASC")
    fun getAllSongsSortedByTitle(): Flow<List<LocalSongEntity>>
}
