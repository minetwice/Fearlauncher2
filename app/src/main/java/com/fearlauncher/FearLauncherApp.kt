package com.fearlauncher

import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fearlauncher.ui.theme.*
import com.fearlauncher.ui.components.BottomNavBar
import com.fearlauncher.ui.screens.*

@Composable
fun FearLauncherApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val context = androidx.compose.ui.platform.LocalContext.current
    val config = remember { com.fearlauncher.logic.ConfigManager.getConfig(context) }
    var isLoggedIn by remember { mutableStateOf(config.selectedUsername.isNotBlank()) }
    var username by remember { mutableStateOf(config.selectedUsername) }
    var isSetupComplete by remember { mutableStateOf(false) } // Should be persisted in real app

    // Toast state
    var toastVisible by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }
    var toastTitle by remember { mutableStateOf("") }

    val infiniteTransition = rememberInfiniteTransition(label = "background")
    val targetOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2000f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(DeepBlack, Color(0xFF1A1A1A), DeepBlack),
                    start = androidx.compose.ui.geometry.Offset(targetOffset / 2, targetOffset / 2),
                    end = androidx.compose.ui.geometry.Offset(targetOffset, targetOffset)
                )
            )
    ) {
        // Glossy Shine Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(SilverGloss.copy(alpha = 0.05f), Color.Transparent),
                        center = androidx.compose.ui.geometry.Offset(targetOffset % 1000f, targetOffset % 1000f),
                        radius = 800f
                    )
                )
        )

        if (!isLoggedIn) {
            LoginScreen(
                onLoginSuccess = { user ->
                    username = user
                    isLoggedIn = true
                }
            )
        } else if (!isSetupComplete) {
            SetupScreen(onComplete = { isSetupComplete = true })
        } else {
            Scaffold(
                containerColor = androidx.compose.ui.graphics.Color.Transparent,
                bottomBar = {
                    BottomNavBar(
                        selectedItem = when (navBackStackEntry?.destination?.route) {
                            "home" -> 0
                            "skins" -> 1
                            "play" -> 2
                            "modpacks" -> 3
                            "settings" -> 4
                            else -> 0
                        },
                        onItemSelected = { index ->
                            val route = when (index) {
                                0 -> "home"
                                1 -> "skins"
                                2 -> "play"
                                3 -> "modpacks"
                                4 -> "settings"
                                else -> "home"
                            }
                            navController.navigate(route) {
                                popUpTo("home") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            HomeScreen(
                                username = username,
                                onLogout = {
                                    com.fearlauncher.logic.ConfigManager.updateConfig(context) { it.copy(selectedUsername = "") }
                                    username = ""
                                    isLoggedIn = false
                                },
                                onAccountSelect = { newUser ->
                                    com.fearlauncher.logic.ConfigManager.updateConfig(context) { it.copy(selectedUsername = newUser) }
                                    username = newUser
                                }
                            )
                        }
                        composable("skins") { SkinScreen() }
                        composable("play") {
                            PlayScreen(
                                onLaunchGame = { version ->
                                    val config = com.fearlauncher.logic.ConfigManager.getConfig(context)
                                    val process = com.fearlauncher.core.LauncherManager.launchGame(
                                        context = context,
                                        versionId = version,
                                        username = username,
                                        maxMemory = config.maxMemory,
                                        renderer = config.renderer,
                                        jvmArgs = config.jvmArgs
                                    )
                                    if (process != null) {
                                        toastTitle = "Game Started!"
                                        toastMessage = "Launching $version..."
                                        toastVisible = true
                                    } else {
                                        toastTitle = "Launch Failed"
                                        toastMessage = "Could not start the game process."
                                        toastVisible = true
                                    }
                                }
                            )
                        }
                        composable("modpacks") { ModpackScreen() }
                        composable("settings") { SettingsScreen() }
                    }
                }
            }
        }

        Box(modifier = Modifier.align(Alignment.TopEnd)) {
            com.fearlauncher.ui.components.AdvancementToast(
                message = toastMessage,
                title = toastTitle,
                isVisible = toastVisible,
                onDismiss = { toastVisible = false }
            )
        }
    }
}
