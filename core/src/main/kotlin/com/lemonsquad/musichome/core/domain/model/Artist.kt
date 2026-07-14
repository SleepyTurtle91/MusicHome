package com.lemonsquad.musichome.core.domain.model

import android.net.Uri

data class Artist(
    val name: String,
    val albumCount: Int,
    val songCount: Int,
    val artworkUri: Uri?
)
