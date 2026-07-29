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
    val repeatMode: Int = 0, // 0: None, 1: One, 2: All
    val shuffleEnabled: Boolean = false,
    val sessionVersion: Int = 1,
    val queueRevision: Int = 0,
    val lastDestination: String?,
    val lastDestinationId: String?,
    val updatedAt: Long
)
