package com.lemonsquad.musichome.core.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class LocalSong(
    @PrimaryKey val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val dataUri: String
)
