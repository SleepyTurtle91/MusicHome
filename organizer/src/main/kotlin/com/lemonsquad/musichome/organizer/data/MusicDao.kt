package com.lemonsquad.musichome.organizer.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicDao {
    @Query("SELECT * FROM organizer_songs ORDER BY title ASC")
    fun getAllSongs(): Flow<List<SongEntity>>

    @Query("SELECT * FROM organizer_albums ORDER BY title ASC")
    fun getAllAlbums(): Flow<List<AlbumEntity>>

    @Query("SELECT * FROM organizer_artists ORDER BY name ASC")
    fun getAllArtists(): Flow<List<ArtistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<SongEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbums(albums: List<AlbumEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtists(artists: List<ArtistEntity>)

    @Update
    suspend fun updateSong(song: SongEntity)

    @Query("SELECT * FROM organizer_songs WHERE id = :id")
    suspend fun getSongById(id: Long): SongEntity?

    @Query("DELETE FROM organizer_songs")
    suspend fun deleteAllSongs()

    @Query("DELETE FROM organizer_albums")
    suspend fun deleteAllAlbums()

    @Query("DELETE FROM organizer_artists")
    suspend fun deleteAllArtists()
}
