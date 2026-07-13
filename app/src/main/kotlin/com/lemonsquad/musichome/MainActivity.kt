package com.lemonsquad.musichome

import android.media.AudioManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.room.Room
import com.lemonsquad.musichome.core.data.LauncherRepositoryImpl
import com.lemonsquad.musichome.core.data.MusicDatabase
import com.lemonsquad.musichome.core.data.MusicRepositoryImpl
import com.lemonsquad.musichome.ui.MusicHomeApp
import com.lemonsquad.musichome.ui.viewmodels.AppsViewModel
import com.lemonsquad.musichome.ui.viewmodels.MusicViewModel

class MainActivity : ComponentActivity() {
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
        
        val musicRepository = MusicRepositoryImpl(database.songDao())
        val launcherRepository = LauncherRepositoryImpl(applicationContext)

        val musicViewModel = MusicViewModel(musicRepository, applicationContext)
        val appsViewModel = AppsViewModel(launcherRepository)

        setContent {
            MusicHomeApp(
                musicViewModel = musicViewModel,
                appsViewModel = appsViewModel
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
