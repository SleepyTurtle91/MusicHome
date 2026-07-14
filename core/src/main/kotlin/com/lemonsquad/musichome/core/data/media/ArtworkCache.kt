package com.lemonsquad.musichome.core.data.media

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class ArtworkCache(private val context: Context) {

    private val artworkDir = File(context.filesDir, "artwork").apply {
        if (!exists()) mkdirs()
    }

    suspend fun getArtwork(albumId: Long, artist: String?): Uri? = withContext(Dispatchers.IO) {
        val artistHash = artist?.hashCode() ?: 0
        val fileName = "album_${albumId}_$artistHash.jpg"
        val cacheFile = File(artworkDir, fileName)

        if (cacheFile.exists()) {
            return@withContext Uri.fromFile(cacheFile)
        }

        // Load from MediaStore and cache it
        val mediaStoreUri = Uri.parse("content://media/external/audio/albumart/$albumId")
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(mediaStoreUri)
            if (inputStream != null) {
                val bitmap = BitmapFactory.decodeStream(inputStream)
                if (bitmap != null) {
                    val resizedBitmap = resizeBitmap(bitmap, 512)
                    saveBitmap(resizedBitmap, cacheFile)
                    return@withContext Uri.fromFile(cacheFile)
                }
            }
        } catch (e: Exception) {
            // Artwork not found or error loading
        }

        null
    }

    private fun resizeBitmap(source: Bitmap, size: Int): Bitmap {
        val width = source.width
        val height = source.height
        val scale = size.toFloat() / Math.max(width, height)
        if (scale >= 1f) return source

        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()
        return Bitmap.createScaledBitmap(source, newWidth, newHeight, true)
    }

    private fun saveBitmap(bitmap: Bitmap, file: File) {
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
        }
    }
}
