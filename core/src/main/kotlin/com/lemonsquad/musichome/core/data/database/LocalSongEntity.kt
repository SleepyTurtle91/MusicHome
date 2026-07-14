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
    val trackNumber: Int = 0
)
