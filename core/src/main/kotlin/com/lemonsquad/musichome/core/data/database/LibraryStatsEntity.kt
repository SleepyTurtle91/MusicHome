package com.lemonsquad.musichome.core.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "library_stats")
data class LibraryStatsEntity(
    @PrimaryKey val id: Int = 0,
    val totalSongs: Int = 0,
    val totalAlbums: Int = 0,
    val totalArtists: Int = 0,
    val missingArtworkCount: Int = 0,
    val duplicateCount: Int = 0,
    val healthScore: Int = 0,
    val lastScanTimestamp: Long = 0
)
