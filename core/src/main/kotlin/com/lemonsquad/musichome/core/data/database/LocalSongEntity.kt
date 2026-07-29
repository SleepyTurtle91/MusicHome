package com.lemonsquad.musichome.core.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_songs")
data class LocalSongEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val albumId: Long?,
    val albumArtUri: String?,
    val duration: Long,
    val path: String,
    val mimeType: String,
    val dateAdded: Long,
    val dateModified: Long = 0,
    val size: Long = 0,
    val trackNumber: Int = 0,
    val year: Int? = null,
    val bitrate: Int = 0,
    val genre: String? = null,
    val replayGain: Float? = null,
    val albumGain: Float? = null,
    val replayPeak: Float? = null,
    val loudnessRange: Float? = null,
    val scanState: Int = 0 // 0: Indexed, 1: Enriched, 2: Analyzed
)
