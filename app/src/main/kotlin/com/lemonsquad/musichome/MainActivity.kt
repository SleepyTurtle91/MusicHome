package com.lemonsquad.musichome

import android.media.AudioManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.room.Room
import com.lemonsquad.musichome.core.data.LauncherRepositoryImpl
import com.lemonsquad.musichome.core.data.database.MusicDatabase
import com.lemonsquad.musichome.core.data.media.MediaStoreScanner
import com.lemonsquad.musichome.core.data.repository.LocalMediaRepository
import com.lemonsquad.musichome.ui.MusicHomeApp
import com.lemonsquad.musichome.ui.viewmodels.AppsViewModel
import com.lemonsquad.musichome.ui.viewmodels.MusicViewModel

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupImmersiveMode()
        volumeControlStream = AudioManager.STREAM_MUSIC

        // Basic Manual DI for the skeleton
        val database = Room.databaseBuilder(
            applicationContext,
            MusicDatabase::class.java,
            "music_db"
        ).build()
        
        val mediaStoreScanner = MediaStoreScanner(applicationContext)
        val musicRepository = LocalMediaRepository(mediaStoreScanner, database.songDao())
        val launcherRepository = LauncherRepositoryImpl(applicationContext)

        val musicViewModel = MusicViewModel(musicRepository, applicationContext)
        val appsViewModel = AppsViewModel(launcherRepository)

        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            MusicHomeApp(
                musicViewModel = musicViewModel,
                appsViewModel = appsViewModel,
                windowSizeClass = windowSizeClass
            )
        }
    }

    private fun setupImmersiveMode() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }
}
