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

    @Query("""
        SELECT 
            albumId as id, 
            album as title, 
            artist, 
            albumArtUri as artworkUri, 
            COUNT(*) as songCount 
        FROM local_songs 
        GROUP BY albumId 
        ORDER BY album ASC
    """)
    fun getAlbums(): Flow<List<AlbumDto>>

    @Query("""
        SELECT 
            artist as name, 
            COUNT(DISTINCT albumId) as albumCount, 
            COUNT(*) as songCount, 
            MAX(albumArtUri) as artworkUri 
        FROM local_songs 
        GROUP BY artist 
        ORDER BY artist ASC
    """)
    fun getArtists(): Flow<List<ArtistDto>>

    @Query("SELECT * FROM local_songs WHERE albumId = :albumId ORDER BY trackNumber ASC, title ASC")
    fun getSongsByAlbum(albumId: Long): Flow<List<LocalSongEntity>>

    @Query("SELECT * FROM local_songs WHERE artist = :artistName ORDER BY album ASC, trackNumber ASC, title ASC")
    fun getSongsByArtist(artistName: String): Flow<List<LocalSongEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePlaybackState(state: PlaybackStateEntity)

    @Query("SELECT * FROM playback_state WHERE id = 0")
    suspend fun getPlaybackState(): PlaybackStateEntity?

    @Query("SELECT * FROM local_songs WHERE id IN (:ids)")
    suspend fun getSongsByIds(ids: List<Long>): List<LocalSongEntity>

    // App Settings
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAppSettings(settings: AppSettingsEntity)

    @Query("SELECT * FROM app_settings WHERE id = 0")
    fun getAppSettings(): Flow<AppSettingsEntity?>

    // Watched Folders
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWatchedFolder(folder: WatchedFolderEntity)

    @Query("SELECT * FROM watched_folders")
    fun getAllWatchedFolders(): Flow<List<WatchedFolderEntity>>

    @Query("DELETE FROM watched_folders WHERE path = :path")
    suspend fun deleteWatchedFolder(path: String)
}

data class AlbumDto(
    val id: Long,
    val title: String,
    val artist: String,
    val artworkUri: String?,
    val songCount: Int
)

data class ArtistDto(
    val name: String,
    val albumCount: Int,
    val songCount: Int,
    val artworkUri: String?
)
