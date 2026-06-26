package com.fearlauncher.ui.components
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.fearlauncher.ui.theme.*
@Composable
fun BottomNavBar(selectedItem: Int, onItemSelected: (Int) -> Unit) {
    NavigationBar(containerColor = BlackSurface, contentColor = SilverPrimary) {
        NavigationBarItem(icon = { Icon(Icons.Default.Home, "Home") }, label = { Text("Home") }, selected = selectedItem == 0, onClick = { onItemSelected(0) }, colors = NavigationBarItemDefaults.colors(selectedTextColor = SilverPrimary, unselectedTextColor = SilverPrimary.copy(alpha = 0.6f), selectedIconColor = SilverPrimary, unselectedIconColor = SilverPrimary.copy(alpha = 0.6f), indicatorColor = BlackSurface))
        NavigationBarItem(icon = { Icon(Icons.Default.PlayArrow, "Play") }, label = { Text("Play") }, selected = selectedItem == 1, onClick = { onItemSelected(1) }, colors = NavigationBarItemDefaults.colors(selectedTextColor = SilverPrimary, unselectedTextColor = SilverPrimary.copy(alpha = 0.6f), selectedIconColor = SilverPrimary, unselectedIconColor = SilverPrimary.copy(alpha = 0.6f), indicatorColor = BlackSurface))
        NavigationBarItem(icon = { Icon(Icons.Default.Settings, "Settings") }, label = { Text("Settings") }, selected = selectedItem == 2, onClick = { onItemSelected(2) }, colors = NavigationBarItemDefaults.colors(selectedTextColor = SilverPrimary, unselectedTextColor = SilverPrimary.copy(alpha = 0.6f), selectedIconColor = SilverPrimary, unselectedIconColor = SilverPrimary.copy(alpha = 0.6f), indicatorColor = BlackSurface))
    }          }
