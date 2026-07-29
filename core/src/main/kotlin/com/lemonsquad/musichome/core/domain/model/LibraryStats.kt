package com.lemonsquad.musichome.core.domain.model

data class LibraryStats(
    val totalSongs: Int = 0,
    val totalAlbums: Int = 0,
    val totalArtists: Int = 0,
    val missingArtworkCount: Int = 0,
    val duplicateCount: Int = 0,
    val healthScore: Int = 0,
    val lastScanTimestamp: Long = 0
)
