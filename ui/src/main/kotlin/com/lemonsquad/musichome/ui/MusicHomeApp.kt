package com.lemonsquad.musichome.ui

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.BatteryManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.navigationsuite.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lemonsquad.musichome.ui.components.BootScreen
import com.lemonsquad.musichome.ui.components.MiniPlayer
import com.lemonsquad.musichome.ui.components.VolumeHud
import com.lemonsquad.musichome.ui.screens.AboutScreen
import com.lemonsquad.musichome.ui.screens.AlbumDetailScreen
import com.lemonsquad.musichome.ui.screens.AppsScreen
import com.lemonsquad.musichome.ui.screens.LibraryScreen
import com.lemonsquad.musichome.ui.screens.PlayerScreen
import com.lemonsquad.musichome.ui.screens.QueueScreen
import com.lemonsquad.musichome.ui.screens.SettingsScreen
import com.lemonsquad.musichome.ui.screens.SoundScreen
import com.lemonsquad.musichome.ui.screens.LibraryToolsScreen
import com.lemonsquad.musichome.ui.screens.DuplicateFinderScreen
import com.lemonsquad.musichome.ui.screens.MetadataEditorScreen
import com.lemonsquad.musichome.ui.screens.ScannerProgressScreen
import com.lemonsquad.musichome.core.domain.model.NavigationMode
import com.lemonsquad.musichome.organizer.ui.LibraryToolsViewModel
import com.lemonsquad.musichome.ui.icons.MusicHomeIcons
import com.lemonsquad.musichome.ui.theme.WalkmanOrange
import com.lemonsquad.musichome.ui.theme.WalkmanTheme
import com.lemonsquad.musichome.ui.viewmodels.AppsViewModel
import com.lemonsquad.musichome.ui.viewmodels.MusicViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicHomeApp(
    musicViewModel: MusicViewModel,
    appsViewModel: AppsViewModel,
    libraryToolsViewModel: LibraryToolsViewModel,
    windowSizeClass: WindowSizeClass,
    appVersion: String
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val context = LocalContext.current
    var currentTime by remember { mutableStateOf(SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())) }
    var batteryLevel by remember { mutableIntStateOf(0) }

    val navigationMode by musicViewModel.navigationMode.collectAsState()
    val playbackStatus by musicViewModel.playbackStatus.collectAsState()

    BackHandler(enabled = currentRoute != "player" && playbackStatus.isPlaying) {
        navController.navigate("player") {
            popUpTo(navController.graph.startDestinationId)
            launchSingleTop = true
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
            batteryLevel = getBatteryLevel(context)
            delay(30000) // Update every 30 seconds
        }
    }

    val navSuiteType = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> NavigationSuiteType.NavigationBar
        else -> when (navigationMode) {
            NavigationMode.AUTO -> {
                if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Medium) {
                    NavigationSuiteType.NavigationRail
                } else {
                    NavigationSuiteType.NavigationDrawer
                }
            }
            NavigationMode.EXPANDED -> NavigationSuiteType.NavigationDrawer
            NavigationMode.COMPACT -> NavigationSuiteType.NavigationRail
        }
    }
    
    val showVolumeHud by musicViewModel.showVolumeHud.collectAsState()
    val volumeLevel by musicViewModel.volumeLevel.collectAsState()
    val isInitialized by musicViewModel.isInitialized.collectAsState()
    val maxVolume = 15 // Standard Android max volume for media, could be dynamic

    WalkmanTheme {
        AnimatedContent(
            targetState = isInitialized,
            transitionSpec = {
                fadeIn(tween(1000)) togetherWith fadeOut(tween(500))
            },
            label = "AppBootstrap"
        ) { initialized ->
            if (!initialized) {
                BootScreen()
            } else {
                NavigationSuiteScaffold(
                    layoutType = navSuiteType,
                    navigationSuiteItems = {
                        val items = listOf(
                            NavigationItem("library", "Library", MusicHomeIcons.Library, MusicHomeIcons.LibraryActive),
                            NavigationItem("player", "Player", MusicHomeIcons.Player, MusicHomeIcons.PlayerActive),
                            NavigationItem("queue", "Queue", MusicHomeIcons.Queue, MusicHomeIcons.QueueActive),
                            NavigationItem("apps", "Apps", MusicHomeIcons.Apps, MusicHomeIcons.AppsActive),
                            NavigationItem("tools", "Tools", MusicHomeIcons.Tools, MusicHomeIcons.ToolsActive),
                            NavigationItem("sound", "Sound", MusicHomeIcons.Sound, MusicHomeIcons.SoundActive),
                            NavigationItem("settings", "Settings", MusicHomeIcons.Settings, MusicHomeIcons.SettingsActive),
                        )

                        items.forEach { item ->
                            val isSelected = currentRoute == item.route
                            item(
                                selected = isSelected,
                                onClick = {
                                    if (currentRoute != item.route) {
                                        navController.navigate(item.route) {
                                            popUpTo(navController.graph.startDestinationId) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                icon = { 
                                    Icon(
                                        if (isSelected) item.activeIcon else item.icon, 
                                        contentDescription = item.label,
                                        tint = if (isSelected) WalkmanOrange else Color.Gray,
                                        modifier = if (isSelected) Modifier.size(28.dp) else Modifier.size(24.dp)
                                    ) 
                                },
                                label = { 
                                    if (navSuiteType != NavigationSuiteType.NavigationRail) {
                                        Text(
                                            item.label.uppercase(), 
                                            fontSize = 10.sp, 
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            letterSpacing = 1.sp,
                                            color = if (isSelected) WalkmanOrange else Color.Gray
                                        ) 
                                    }
                                }
                            )
                        }
                    },
                    containerColor = Color.Black,
                    contentColor = Color.White
                ) {
                    Scaffold(
                        topBar = {
                            CenterAlignedTopAppBar(
                                navigationIcon = {
                                    if (navSuiteType != NavigationSuiteType.NavigationBar) {
                                        IconButton(onClick = { musicViewModel.toggleNavigationMode() }) {
                                            Icon(MusicHomeIcons.Menu, contentDescription = "Toggle Navigation", tint = Color.White)
                                        }
                                    }
                                },
                                title = {
                                    Text(
                                        "WALKMAN",
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 4.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                },
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                    containerColor = Color.Black
                                ),
                                actions = {
                                    Text(
                                        "$currentTime | $batteryLevel%",
                                        modifier = Modifier.padding(end = 16.dp),
                                        fontSize = 12.sp,
                                        color = Color.LightGray
                                    )
                                }
                            )
                        },
                        bottomBar = {
                            if (currentRoute != "player" && currentRoute != "album-detail" && currentRoute != null) {
                                MiniPlayer(
                                    viewModel = musicViewModel,
                                    onClick = { navController.navigate("player") }
                                )
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = "player",
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable("library") { 
                                LibraryScreen(
                                    viewModel = musicViewModel,
                                    onAlbumClick = { album ->
                                        musicViewModel.selectAlbum(album)
                                        navController.navigate("album-detail")
                                    },
                                    onEditMetadata = { song ->
                                        navController.navigate("metadata?path=${Uri.encode(song.path)}")
                                    }
                                ) 
                            }
                            composable("album-detail") {
                                AlbumDetailScreen(musicViewModel)
                            }
                            composable("queue") {
                                QueueScreen(musicViewModel)
                            }
                            composable("player") { PlayerScreen(musicViewModel) }
                            composable("apps") { AppsScreen(appsViewModel) }
                            composable("tools") { 
                                LibraryToolsScreen(
                                    viewModel = libraryToolsViewModel,
                                    onNavigateToDuplicates = { navController.navigate("duplicates") },
                                    onNavigateToScanner = { navController.navigate("scanner") }
                                ) 
                            }
                            composable("duplicates") {
                                DuplicateFinderScreen(
                                    viewModel = libraryToolsViewModel,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                            composable("scanner") {
                                ScannerProgressScreen(
                                    viewModel = libraryToolsViewModel,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                            composable(
                                route = "metadata?path={path}",
                                arguments = listOf(
                                    navArgument("path") { type = NavType.StringType }
                                )
                            ) { backStackEntry ->
                                val path = backStackEntry.arguments?.getString("path")
                                var songEntity by remember { mutableStateOf<com.lemonsquad.musichome.organizer.data.SongEntity?>(null) }
                                
                                LaunchedEffect(path) {
                                    if (path != null) {
                                        songEntity = libraryToolsViewModel.getSongByPath(path)
                                    }
                                }

                                if (songEntity != null) {
                                    MetadataEditorScreen(
                                        song = songEntity!!,
                                        viewModel = libraryToolsViewModel,
                                        onSaved = { navController.popBackStack() }
                                    )
                                } else {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator(color = WalkmanOrange)
                                    }
                                }
                            }
                            composable("sound") { SoundScreen(musicViewModel) }
                            composable("settings") { 
                                SettingsScreen(
                                    onNavigateToAbout = { navController.navigate("about") }
                                ) 
                            }
                            composable("about") { AboutScreen(musicViewModel, appVersion) }
                        }
                    }
                    
                    VolumeHud(
                        visible = showVolumeHud,
                        volume = volumeLevel,
                        maxVolume = maxVolume
                    )
                }
            }
        }
    }
}

private fun getBatteryLevel(context: Context): Int {
    val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
    val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
    return if (level >= 0 && scale > 0) (level * 100 / scale) else 0
}

@Composable
fun MusicBottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.Black,
        tonalElevation = 0.dp
    ) {
        val navItems = listOf(
            NavigationItem("library", "Library", MusicHomeIcons.Library, MusicHomeIcons.LibraryActive),
            NavigationItem("player", "Player", MusicHomeIcons.Player, MusicHomeIcons.PlayerActive),
            NavigationItem("queue", "Queue", MusicHomeIcons.Queue, MusicHomeIcons.QueueActive),
            NavigationItem("apps", "Apps", MusicHomeIcons.Apps, MusicHomeIcons.AppsActive),
            NavigationItem("tools", "Tools", MusicHomeIcons.Tools, MusicHomeIcons.ToolsActive),
            NavigationItem("sound", "Sound", MusicHomeIcons.Sound, MusicHomeIcons.SoundActive),
            NavigationItem("settings", "Settings", MusicHomeIcons.Settings, MusicHomeIcons.SettingsActive)
        )

        navItems.forEach { item ->
            val isSelected = currentDestination == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentDestination != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { 
                    Icon(
                        if (isSelected) item.activeIcon else item.icon, 
                        contentDescription = item.label,
                        tint = if (isSelected) WalkmanOrange else Color.Gray
                    ) 
                },
                label = { 
                    Text(
                        item.label.uppercase(), 
                        fontSize = 10.sp,
                        color = if (isSelected) WalkmanOrange else Color.Gray,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    ) 
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = WalkmanOrange,
                    selectedTextColor = WalkmanOrange,
                    indicatorColor = Color.Transparent,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}

private data class NavigationItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val activeIcon: androidx.compose.ui.graphics.vector.ImageVector
)
