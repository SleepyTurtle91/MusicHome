package com.lemonsquad.musichome.core.data.media

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.lemonsquad.musichome.core.data.database.LocalSongEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class ManualScanner(private val context: Context) {

    private val supportedExtensions = setOf("mp3", "flac", "wav", "m4a", "ogg")

    suspend fun scanFolder(folderPath: String): List<LocalSongEntity> = withContext(Dispatchers.IO) {
        val root = File(folderPath)
        if (!root.exists() || !root.isDirectory) return@withContext emptyList<LocalSongEntity>()

        val songs = mutableListOf<LocalSongEntity>()
        scanRecursive(root, songs)
        songs
    }

    private fun scanRecursive(directory: File, songs: MutableList<LocalSongEntity>) {
        val files = directory.listFiles() ?: return
        for (file in files) {
            if (file.isDirectory) {
                scanRecursive(file, songs)
            } else if (file.extension.lowercase() in supportedExtensions) {
                extractMetadata(file)?.let { songs.add(it) }
            }
        }
    }

    private fun extractMetadata(file: File): LocalSongEntity? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(file.absolutePath)
            
            val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: file.nameWithoutExtension
            val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: "Unknown Artist"
            val album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM) ?: "Unknown Album"
            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0L
            val trackNumber = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER)
                ?.substringBefore("/")?.toIntOrNull() ?: 0

            // Generate a stable ID based on path hash
            val id = file.absolutePath.hashCode().toLong()

            LocalSongEntity(
                id = id,
                title = title,
                artist = artist,
                album = album,
                albumId = album.hashCode().toLong(), // Simulated albumId for manual files
                albumArtUri = null, // Manual extraction for art could be added later
                duration = duration,
                path = file.absolutePath,
                mimeType = "audio/*",
                dateAdded = file.lastModified(),
                trackNumber = trackNumber
            )
        } catch (e: Exception) {
            null
        } finally {
            retriever.release()
        }
    }
}
