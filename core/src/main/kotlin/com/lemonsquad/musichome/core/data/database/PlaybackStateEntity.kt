package com.lemonsquad.musichome.core.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playback_state")
data class PlaybackStateEntity(
    @PrimaryKey val id: Int = 0, // Single row for current state
    val lastSongId: Long?,
    val positionMs: Long,
    val queueIds: String, // Comma-separated IDs
    val queueIndex: Int,
    val source: String?,
    val sourceName: String?,
    val lastDestination: String?,
    val lastDestinationId: String?,
    val updatedAt: Long
)
