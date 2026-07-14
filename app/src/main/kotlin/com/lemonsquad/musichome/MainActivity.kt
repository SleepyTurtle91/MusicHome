package com.lemonsquad.musichome

import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
    private lateinit var musicViewModel: MusicViewModel

    private val directoryPicker = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri: Uri? ->
        uri?.let {
            val contentResolver = applicationContext.contentResolver
            contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            
            // For SD cards on Android 7+, SAF URIs are the only reliable way
            // to access files. We'll store the URI string directly.
            musicViewModel.repository.addManualPath(it.toString())
        }
    }

    private fun resolveUriToPath(uri: Uri): String? {
        val documentId = uri.path?.split(":")?.lastOrNull() ?: return null
        
        // Handle primary storage
        if (uri.toString().contains("primary")) {
            return "${Environment.getExternalStorageDirectory()}/$documentId"
        }
        
        // Handle SD Cards (Secondary Storage)
        val volumes = applicationContext.getExternalFilesDirs(null)
        for (volume in volumes) {
            val root = volume.absolutePath.split("/Android").firstOrNull()
            if (root != null && !root.contains("emulated")) {
                val candidate = "$root/$documentId"
                if (java.io.File(candidate).exists()) return candidate
            }
        }
        
        return null
    }

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
        ).fallbackToDestructiveMigration().build()
        
        val mediaStoreScanner = MediaStoreScanner(applicationContext)
        val musicRepository = LocalMediaRepository(mediaStoreScanner, database.songDao(), applicationContext)
        val launcherRepository = LauncherRepositoryImpl(applicationContext)

        musicViewModel = MusicViewModel(musicRepository, applicationContext)
        musicViewModel.setDirectoryPicker { directoryPicker.launch(null) }
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> {
                musicViewModel.adjustVolume(1)
                true
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                musicViewModel.adjustVolume(-1)
                true
            }
            else -> super.onKeyDown(keyCode, event)
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
