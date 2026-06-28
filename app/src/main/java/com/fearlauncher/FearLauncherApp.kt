package com.fearlauncher

import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
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
    var isLoggedIn by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }

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
                onMicrosoftLogin = {
                    username = "MicrosoftUser"
                    isLoggedIn = true
                },
                onLocalLogin = { user ->
                    username = user
                    isLoggedIn = true
                }
            )
        } else {
            Scaffold(
                containerColor = androidx.compose.ui.graphics.Color.Transparent,
                bottomBar = {
                    BottomNavBar(
                        selectedItem = when (navBackStackEntry?.destination?.route) {
                            "home" -> 0
                            "play" -> 1
                            "modpacks" -> 2
                            "settings" -> 3
                            else -> 0
                        },
                        onItemSelected = { index ->
                            val route = when (index) {
                                0 -> "home"
                                1 -> "play"
                                2 -> "modpacks"
                                3 -> "settings"
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
                        composable("home") { HomeScreen(username = username) }
                        composable("play") { PlayScreen(onLaunchGame = { version -> /* Launch logic */ }) }
                        composable("modpacks") { ModpackScreen() }
                        composable("settings") { SettingsScreen() }
                    }
                }
            }
        }
    }
}
