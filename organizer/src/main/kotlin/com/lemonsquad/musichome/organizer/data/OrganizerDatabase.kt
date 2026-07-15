package com.lemonsquad.musichome.organizer.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SongEntity::class, AlbumEntity::class, ArtistEntity::class], version = 1, exportSchema = false)
abstract class OrganizerDatabase : RoomDatabase() {
    abstract fun musicDao(): MusicDao

    companion object {
        @Volatile
        private var INSTANCE: OrganizerDatabase? = null

        fun getDatabase(context: Context): OrganizerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OrganizerDatabase::class.java,
                    "organizer_database"
                ).fallbackToDestructiveMigration(true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
