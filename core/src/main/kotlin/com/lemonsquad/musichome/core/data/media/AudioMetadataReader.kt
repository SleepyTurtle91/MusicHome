package com.lemonsquad.musichome.core.data.media

import android.net.Uri
import com.lemonsquad.musichome.core.domain.model.AudioMetadata

interface AudioMetadataReader {
    fun readMetadata(uri: Uri): AudioMetadata
}
