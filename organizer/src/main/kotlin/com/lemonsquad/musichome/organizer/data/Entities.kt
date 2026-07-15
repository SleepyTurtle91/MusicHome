package com.lemonsquad.musichome.organizer.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "organizer_songs")
data class SongEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val path: String,
    val format: String,
    val bitrate: Int,
    val trackNumber: Int?,
    val year: Int?,
    val genre: String?,
    val artworkPath: String?,
    val size: Long,
    val dateAdded: Long
)

@Entity(tableName = "organizer_albums")
data class AlbumEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val artist: String,
    val year: Int?,
    val artworkPath: String?
)

@Entity(tableName = "organizer_artists")
data class ArtistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)
