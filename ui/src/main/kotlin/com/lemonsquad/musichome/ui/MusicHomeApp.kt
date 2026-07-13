package com.lemonsquad.musichome.ui

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lemonsquad.musichome.ui.components.MiniPlayer
import com.lemonsquad.musichome.ui.screens.AppsScreen
import com.lemonsquad.musichome.ui.screens.LibraryScreen
import com.lemonsquad.musichome.ui.screens.PlayerScreen
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
    appsViewModel: AppsViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val context = LocalContext.current
    var currentTime by remember { mutableStateOf(SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())) }
    var batteryLevel by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
            batteryLevel = getBatteryLevel(context)
            delay(30000) // Update every 30 seconds
        }
    }

    WalkmanTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
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
                Column {
                    if (currentRoute != "player" && currentRoute != null) {
                        MiniPlayer(viewModel = musicViewModel)
                    }
                    MusicBottomBar(navController)
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "player",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("library") { LibraryScreen(musicViewModel) }
                composable("player") { PlayerScreen(musicViewModel) }
                composable("apps") { AppsScreen(appsViewModel) }
                composable("sound") { Box(modifier = Modifier.padding(16.dp)) { Text("Sound Settings Placeholder", color = Color.White) } }
                composable("settings") { Box(modifier = Modifier.padding(16.dp)) { Text("Settings Placeholder", color = Color.White) } }
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
        val items = listOf(
            Triple("library", "Library", Icons.Default.LibraryMusic),
            Triple("player", "Player", Icons.Default.PlayArrow),
            Triple("apps", "Apps", Icons.Default.Apps),
            Triple("sound", "Sound", Icons.Default.Tune),
            Triple("settings", "Settings", Icons.Default.Settings)
        )

        items.forEach { (route, label, icon) ->
            NavigationBarItem(
                selected = currentDestination == route,
                onClick = {
                    if (currentDestination != route) {
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label, fontSize = 10.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = Color.Transparent,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}
