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
import com.lemonsquad.musichome.core.data.LauncherRepositoryImpl
import com.lemonsquad.musichome.core.domain.repository.MusicRepositoryProvider
import com.lemonsquad.musichome.organizer.ui.LibraryToolsViewModel
import com.lemonsquad.musichome.ui.MusicHomeApp
import com.lemonsquad.musichome.ui.viewmodels.AppsViewModel
import com.lemonsquad.musichome.ui.viewmodels.MusicViewModel

class MainActivity : ComponentActivity() {
    private lateinit var musicViewModel: MusicViewModel

    private val audioPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            musicViewModel.syncLibrary()
        }
    }

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

    private fun checkAndRequestAudioPermission() {
        val permission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            android.Manifest.permission.READ_MEDIA_AUDIO
        } else {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (androidx.core.content.ContextCompat.checkSelfPermission(this, permission) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            musicViewModel.syncLibrary()
        } else {
            audioPermissionLauncher.launch(permission)
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

        // L.I.S.A. A1: Retrieve process-scoped singleton repository from Application container
        val musicRepository = (application as MusicRepositoryProvider).musicRepository
        val launcherRepository = LauncherRepositoryImpl(applicationContext)

        val libraryToolsViewModel = LibraryToolsViewModel(musicRepository)

        musicViewModel = MusicViewModel(musicRepository, applicationContext)
        musicViewModel.setDirectoryPicker { directoryPicker.launch(null) }
        val appsViewModel = AppsViewModel(launcherRepository)

        // L.I.S.A. A2: Request runtime audio storage permission on first launch
        checkAndRequestAudioPermission()

        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            MusicHomeApp(
                musicViewModel = musicViewModel,
                appsViewModel = appsViewModel,
                libraryToolsViewModel = libraryToolsViewModel,
                windowSizeClass = windowSizeClass,
                appVersion = BuildConfig.VERSION_NAME
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
