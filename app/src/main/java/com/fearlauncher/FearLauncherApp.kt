package com.fearlauncher

import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    // JRE initialization state
    var jreDownloadMessage by remember { mutableStateOf("") }
    var jreDownloadProgress by remember { mutableStateOf(0f) }
    var isJreInitializing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val jreManager = com.fearlauncher.core.JREManager(context)
        val versions = com.fearlauncher.core.JREManager.JREVersion.entries

        val missingVersions = versions.filter { !jreManager.isJREInstalled(it) }
        if (missingVersions.isNotEmpty()) {
            isJreInitializing = true
            for (version in missingVersions) {
                jreManager.downloadAndExtractJRE(version) { msg, progress ->
                    jreDownloadMessage = msg
                    jreDownloadProgress = progress
                }
            }
            isJreInitializing = false
        }
    }

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
            .graphicsLayer {
                scaleX = config.guiScale
                scaleY = config.guiScale
            }
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
                            "installations" -> 2
                            "play" -> 3
                            "modpacks" -> 4
                            "settings" -> 5
                            else -> 0
                        },
                        onItemSelected = { index ->
                            val route = when (index) {
                                0 -> "home"
                                1 -> "skins"
                                2 -> "installations"
                                3 -> "play"
                                4 -> "modpacks"
                                5 -> "settings"
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
                                composable("installations") { InstallationScreen() }
                        composable("play") {
                            PlayScreen(
                                onLaunchGame = { version ->
                                    val currentConfig = com.fearlauncher.logic.ConfigManager.getConfig(context)
                                    val process = com.fearlauncher.core.LauncherManager.launchGame(
                                        context = context,
                                        versionId = version,
                                        username = username,
                                        maxMemory = currentConfig.maxMemory,
                                        renderer = currentConfig.renderer,
                                        javaPath = currentConfig.javaPath,
                                        jvmArgs = currentConfig.jvmArgs
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

        // JRE Progress Bar at bottom
        if (isJreInitializing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 0.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DeepBlack.copy(alpha = 0.8f))
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        jreDownloadMessage,
                        color = SilverPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = jreDownloadProgress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp),
                        color = SilverPrimary,
                        trackColor = SilverDark.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}
