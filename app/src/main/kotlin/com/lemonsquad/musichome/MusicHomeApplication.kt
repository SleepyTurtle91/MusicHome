package com.lemonsquad.musichome

import android.app.Application
import androidx.room.Room
import com.lemonsquad.musichome.core.data.database.MusicDatabase
import com.lemonsquad.musichome.core.data.media.MediaStoreScanner
import com.lemonsquad.musichome.core.data.repository.LocalMediaRepository
import com.lemonsquad.musichome.core.domain.repository.MusicRepository
import com.lemonsquad.musichome.core.domain.repository.MusicRepositoryProvider

/**
 * L.I.S.A. Workstream A1: Process-Scoped Singleton Container
 *
 * Owns the single [MusicDatabase] and [LocalMediaRepository] instance for the
 * entire application process. Both [MainActivity] and
 * [com.lemonsquad.musichome.media.player.MusicPlaybackService] retrieve the
 * shared repository via `(applicationContext as MusicRepositoryProvider).musicRepository`.
 *
 * This eliminates:
 * - Dual Room connection pools competing for SQLite write locks
 * - Duplicate MediaStoreObserver registrations causing parallel scan storms
 * - Desynchronized StateFlows between UI and Service
 */
class MusicHomeApplication : Application(), MusicRepositoryProvider {

    override val musicRepository: MusicRepository by lazy {
        val database = Room.databaseBuilder(
            this,
            MusicDatabase::class.java,
            "music_db"
        ).build()

        val mediaStoreScanner = MediaStoreScanner(this)
        LocalMediaRepository(mediaStoreScanner, database.songDao(), this)
    }
}
