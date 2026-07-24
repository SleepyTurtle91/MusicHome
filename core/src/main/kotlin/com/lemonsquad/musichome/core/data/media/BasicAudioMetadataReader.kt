package com.lemonsquad.musichome.core.data.media

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.lemonsquad.musichome.core.domain.model.AudioMetadata

class BasicAudioMetadataReader(private val context: Context) : AudioMetadataReader {
    override fun readMetadata(uri: Uri): AudioMetadata {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(context, uri)
            
            val sampleRate = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_SAMPLERATE)?.toIntOrNull()
            } else null
            
            val bitRate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)?.toIntOrNull()
            
            // ReplayGain is often stored in custom tags or Vorbis comments.
            // Standard Android retriever might not see it, but we prepare the slots.
            
            AudioMetadata(
                sampleRate = sampleRate,
                format = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE),
                // Bit depth heuristics for standard formats
                bitDepth = if ((bitRate ?: 0) > 1500000) 24 else 16 
            )
        } catch (e: Exception) {
            AudioMetadata()
        } finally {
            retriever.release()
        }
    }
}
