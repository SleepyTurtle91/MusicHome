package com.lemonsquad.musichome.core.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watched_folders")
data class WatchedFolderEntity(
    @PrimaryKey val path: String,
    val dateAdded: Long = System.currentTimeMillis()
)
