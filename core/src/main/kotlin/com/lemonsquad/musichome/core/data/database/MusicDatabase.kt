package com.lemonsquad.musichome.core.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        LocalSongEntity::class, 
        PlaybackStateEntity::class, 
        WatchedFolderEntity::class,
        AppSettingsEntity::class,
        LibraryStatsEntity::class
    ], 
    version = 6, 
    exportSchema = true
)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
}
