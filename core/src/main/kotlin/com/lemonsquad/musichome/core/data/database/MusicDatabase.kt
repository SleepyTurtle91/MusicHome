package com.lemonsquad.musichome.core.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [LocalSongEntity::class, PlaybackStateEntity::class, WatchedFolderEntity::class], version = 3, exportSchema = false)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
}
