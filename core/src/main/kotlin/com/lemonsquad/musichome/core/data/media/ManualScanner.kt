package com.lemonsquad.musichome.core.data.media

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.lemonsquad.musichome.core.data.database.LocalSongEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class ManualScanner(private val context: Context) {

    private val supportedExtensions = setOf("mp3", "flac", "wav", "m4a", "ogg")

    suspend fun scanFolder(folderPath: String): List<LocalSongEntity> = withContext(Dispatchers.IO) {
        val songs = mutableListOf<LocalSongEntity>()
        
        if (folderPath.startsWith("content://")) {
            // Scan using Storage Access Framework (SAF)
            val rootUri = Uri.parse(folderPath)
            val rootDoc = DocumentFile.fromTreeUri(context, rootUri)
            if (rootDoc != null && rootDoc.isDirectory) {
                scanRecursiveSAF(rootDoc, songs)
            }
        } else {
            // Scan using direct File API
            val root = File(folderPath)
            if (root.exists() && root.isDirectory) {
                scanRecursiveFile(root, songs)
            }
        }
        
        songs
    }

    private fun scanRecursiveFile(directory: File, songs: MutableList<LocalSongEntity>) {
        val files = directory.listFiles() ?: return
        for (file in files) {
            if (file.isDirectory) {
                scanRecursiveFile(file, songs)
            } else if (file.extension.lowercase() in supportedExtensions) {
                extractMetadataFromFile(file)?.let { songs.add(it) }
            }
        }
    }

    private fun scanRecursiveSAF(directory: DocumentFile, songs: MutableList<LocalSongEntity>) {
        val files = directory.listFiles()
        for (file in files) {
            if (file.isDirectory) {
                scanRecursiveSAF(file, songs)
            } else {
                val extension = file.name?.substringAfterLast(".", "")?.lowercase()
                if (extension in supportedExtensions) {
                    extractMetadataFromSAF(file)?.let { songs.add(it) }
                }
            }
        }
    }

    private fun extractMetadataFromFile(file: File): LocalSongEntity? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(file.absolutePath)
            createEntity(retriever, file.absolutePath, file.nameWithoutExtension, file.lastModified())
        } catch (e: Exception) {
            null
        } finally {
            retriever.release()
        }
    }

    private fun extractMetadataFromSAF(file: DocumentFile): LocalSongEntity? {
        val retriever = MediaMetadataRetriever()
        return try {
            context.contentResolver.openFileDescriptor(file.uri, "r")?.use { pfd ->
                retriever.setDataSource(pfd.fileDescriptor)
                createEntity(retriever, file.uri.toString(), file.name?.substringBeforeLast(".") ?: "Unknown", file.lastModified())
            }
        } catch (e: Exception) {
            null
        } finally {
            retriever.release()
        }
    }

    private fun createEntity(
        retriever: MediaMetadataRetriever, 
        path: String, 
        fallbackTitle: String,
        lastModified: Long
    ): LocalSongEntity {
        val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: fallbackTitle
        val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: "Unknown Artist"
        val album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM) ?: "Unknown Album"
        val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0L
        val trackNumber = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER)
            ?.substringBefore("/")?.toIntOrNull() ?: 0

        return LocalSongEntity(
            id = path.hashCode().toLong(),
            title = title,
            artist = artist,
            album = album,
            albumId = album.hashCode().toLong(),
            albumArtUri = null,
            duration = duration,
            path = path,
            mimeType = "audio/*",
            dateAdded = lastModified,
            trackNumber = trackNumber
        )
    }
}
