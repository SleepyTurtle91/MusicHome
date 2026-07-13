package com.lemonsquad.musichome.core.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lemonsquad.musichome.core.domain.LocalSong

@Database(entities = [LocalSong::class], version = 1, exportSchema = false)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
}
