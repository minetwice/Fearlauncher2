package com.fearlauncher

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import com.fearlauncher.ui.components.BottomNavBar
import com.fearlauncher.ui.screens.*

@Composable
fun FearLauncherApp() {
    var selectedItem by remember { mutableStateOf(0) }
    var isLoggedIn by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }

    if (!isLoggedIn) {
        LoginScreen(
            onMicrosoftLogin = {
                // Microsoft login logic here
                isLoggedIn = true
                username = "MicrosoftUser"
            },
            onLocalLogin = { user ->
                // Local login logic here
                isLoggedIn = true
                username = user
            }
        )
    } else {
        Scaffold(
            bottomBar = { 
                BottomNavBar(selectedItem = selectedItem, onItemSelected = { selectedItem = it }) 
            }
        ) { innerPadding ->
            when (selectedItem) {
                0 -> HomeScreen(username = username)
                1 -> PlayScreen(onLaunchGame = { version -> /* Launch logic */ })
                2 -> SettingsScreen()
            }
        }
    }
}
