package com.lemonsquad.musichome.core.domain.model

import android.net.Uri

data class Album(
    val id: Long,
    val title: String,
    val artist: String,
    val artworkUri: Uri?,
    val songCount: Int,
    val year: Int? = null
)
