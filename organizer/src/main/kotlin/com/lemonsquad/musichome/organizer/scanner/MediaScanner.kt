package com.lemonsquad.musichome.organizer.scanner

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import com.lemonsquad.musichome.organizer.data.AlbumEntity
import com.lemonsquad.musichome.organizer.data.ArtistEntity
import com.lemonsquad.musichome.organizer.data.SongEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

class MediaScanner(private val context: Context) {

    suspend fun scanLocalMedia(): List<SongEntity> = withContext(Dispatchers.IO) {
        Log.d("MediaScanner", "Starting media scan...")
        val songs = mutableListOf<SongEntity>()
        
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.ALBUM_ID
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        try {
            context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
                val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
                val trackColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
                val yearColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)
                val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

                while (cursor.moveToNext()) {
                    ensureActive()
                    val title = cursor.getString(titleColumn) ?: "Unknown Title"
                    val artist = cursor.getString(artistColumn) ?: "Unknown Artist"
                    val album = cursor.getString(albumColumn) ?: "Unknown Album"
                    val duration = cursor.getLong(durationColumn)
                    val path = cursor.getString(pathColumn)
                    val mimeType = cursor.getString(mimeTypeColumn) ?: "audio/unknown"
                    val size = cursor.getLong(sizeColumn)
                    val dateAdded = cursor.getLong(dateAddedColumn)
                    val track = cursor.getInt(trackColumn)
                    val year = cursor.getInt(yearColumn)
                    val albumId = cursor.getLong(albumIdColumn)

                    val artworkUri = try {
                        ContentUris.withAppendedId(
                            android.net.Uri.parse("content://media/external/audio/albumart"),
                            albumId
                        ).toString()
                    } catch (e: Exception) {
                        null
                    }

                    songs.add(
                        SongEntity(
                            title = title,
                            artist = artist,
                            album = album,
                            duration = duration,
                            path = path,
                            format = mimeType,
                            bitrate = 0,
                            trackNumber = if (track > 0) track else null,
                            year = if (year > 0) year else null,
                            genre = null,
                            artworkPath = artworkUri,
                            size = size,
                            dateAdded = dateAdded
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("MediaScanner", "Error querying MediaStore", e)
        }
        songs
    }

    fun extractAlbumsAndArtists(songs: List<SongEntity>): Pair<List<AlbumEntity>, List<ArtistEntity>> {
        val albums = songs.distinctBy { it.album + it.artist }.map {
            AlbumEntity(
                title = it.album,
                artist = it.artist,
                year = it.year,
                artworkPath = it.artworkPath
            )
        }
        val artists = songs.distinctBy { it.artist }.map {
            ArtistEntity(name = it.artist)
        }
        return Pair(albums, artists)
    }
}
