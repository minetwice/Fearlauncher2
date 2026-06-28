@file:OptIn(ExperimentalMaterial3Api::class)
package com.fearlauncher.ui.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.fearlauncher.logic.VersionManager
import com.fearlauncher.network.NetworkModule
import com.fearlauncher.ui.theme.*
import kotlinx.coroutines.launch

data class MinecraftVersion(
    val id: String,
    val name: String,
    val type: String,
    val isInstalled: Boolean = false
)

@Composable
fun PlayScreen(
    onLaunchGame: (String) -> Unit
) {
    var selectedVersion by remember { mutableStateOf<MinecraftVersion?>(null) }
    var expandedDropdown by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var isDownloading by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableFloatStateOf(0f) }
    var availableVersions by remember { mutableStateOf<List<MinecraftVersion>>(emptyList()) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val manifest = NetworkModule.minecraftApi.getVersionManifest()
            availableVersions = manifest.versions.map { v ->
                MinecraftVersion(
                    id = v.id,
                    name = "${v.id} - ${v.type.replaceFirstChar { it.uppercase() }}",
                    type = v.type,
                    isInstalled = false // Will be updated by VersionManager
                )
            }
        } catch (e: Exception) {
            // Handle error
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Header Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "PLAY MINECRAFT",
                    style = MaterialTheme.typography.headlineMedium,
                    color = SilverAccent,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Select your version and launch",
                    color = SilverDark,
                    fontSize = 14.sp
                )
            }
            
            // Version Selector Dropdown
            Box {
                OutlinedButton(
                    onClick = { expandedDropdown = true },
                    modifier = Modifier.width(220.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = SilverPrimary
                    )
                ) {
                    Icon(Icons.Default.List, "Versions", tint = SilverPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        selectedVersion?.id ?: "Select Version",
                        color = SilverPrimary,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Default.ArrowDropDown, null, tint = SilverPrimary)
                }

                DropdownMenu(
                    expanded = expandedDropdown,
                    onDismissRequest = { expandedDropdown = false },
                    modifier = Modifier.background(BlackSurface).width(220.dp)
                ) {
                    availableVersions.take(10).forEach { version ->
                        DropdownMenuItem(
                            text = { Text(version.id, color = SilverPrimary) },
                            onClick = {
                                selectedVersion = version
                                expandedDropdown = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Main Content Area
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            // Left Panel - Version List
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BlackSurface.copy(alpha = 0.8f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        "Available Versions",
                        style = MaterialTheme.typography.titleMedium,
                        color = SilverAccent,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(availableVersions) { version ->
                            VersionListItem(
                                version = version,
                                isSelected = selectedVersion?.id == version.id,
                                onSelect = { selectedVersion = version }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(24.dp))

            // Right Panel - Launch Controls
            Column(
                modifier = Modifier
                    .width(300.dp)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (selectedVersion != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = BlackSurface.copy(alpha = 0.8f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Gamepad,
                                "Game",
                                tint = SilverPrimary,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                selectedVersion!!.name,
                                style = MaterialTheme.typography.titleLarge,
                                color = SilverPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                selectedVersion!!.type,
                                color = SilverDark,
                                fontSize = 12.sp
                            )
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            // Memory Allocation
                            Text(
                                "Memory Allocation",
                                color = SilverDark,
                                fontSize = 12.sp
                            )
                            Slider(
                                value = 4f,
                                onValueChange = { },
                                valueRange = 2f..8f,
                                steps = 5,
                                colors = SliderDefaults.colors(
                                    thumbColor = SilverPrimary,
                                    activeTrackColor = SilverPrimary,
                                    inactiveTrackColor = SilverDark
                                )
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("2GB", color = SilverDark, fontSize = 10.sp)
                                Text("4GB", color = SilverPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("8GB", color = SilverDark, fontSize = 10.sp)
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            // Download Progress
                            if (isDownloading) {
                                LinearProgressIndicator(
                                    progress = downloadProgress,
                                    modifier = Modifier.fillMaxWidth().height(8.dp).padding(vertical = 8.dp),
                                    color = SilverPrimary,
                                    trackColor = SilverDark.copy(alpha = 0.3f)
                                )
                                Text(
                                    "Downloading... ${(downloadProgress * 100).toInt()}%",
                                    color = SilverPrimary,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            // Launch Button
                            Button(
                                onClick = {
                                    if (selectedVersion!!.isInstalled) {
                                        isLoading = true
                                        onLaunchGame(selectedVersion!!.id)
                                        isLoading = false
                                    } else {
                                        isDownloading = true
                                        scope.launch {
                                            try {
                                                // In a real scenario, we'd get the actual JAR URL from the manifest
                                                val manifest = NetworkModule.minecraftApi.getVersionManifest()
                                                val versionInfo = manifest.versions.find { it.id == selectedVersion!!.id }

                                                val detail = NetworkModule.minecraftApi.getVersionDetail(versionInfo?.url ?: "")

                                                VersionManager.downloadVersion(
                                                    context = context,
                                                    versionId = selectedVersion!!.id,
                                                    clientJarUrl = detail.downloads.client.url,
                                                    onProgress = { progress ->
                                                        downloadProgress = progress
                                                    }
                                                )
                                                // Refresh versions list to show as installed
                                                availableVersions = availableVersions.map {
                                                    if (it.id == selectedVersion!!.id) it.copy(isInstalled = true) else it
                                                }
                                                selectedVersion = selectedVersion?.copy(isInstalled = true)
                                            } catch (e: Exception) {
                                                // Handle error
                                            } finally {
                                                isDownloading = false
                                                downloadProgress = 0f
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                enabled = !isLoading && !isDownloading,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedVersion!!.isInstalled) SilverPrimary else SilverDark
                                )
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = BlackBg,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        if (selectedVersion!!.isInstalled) Icons.Default.PlayArrow else Icons.Default.Download,
                                        "Action",
                                        tint = if (selectedVersion!!.isInstalled) BlackBg else SilverDark,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        if (selectedVersion!!.isInstalled) "LAUNCH GAME" else "INSTALL VERSION",
                                        color = if (selectedVersion!!.isInstalled) BlackBg else SilverDark,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = BlackSurface.copy(alpha = 0.8f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Select a version to continue",
                                color = SilverDark,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quick Actions
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = BlackSurface.copy(alpha = 0.8f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            "Quick Actions",
                            style = MaterialTheme.typography.titleSmall,
                            color = SilverAccent,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ActionButton(icon = Icons.Default.FolderOpen, label = "Mods")
                            ActionButton(icon = Icons.Default.Palette, label = "Resource Packs")
                            ActionButton(icon = Icons.Default.Build, label = "Settings")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VersionListItem(
    version: MinecraftVersion,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) SilverPrimary.copy(alpha = 0.2f) 
                          else BlackSurface.copy(alpha = 0.5f)
        ),
        onClick = onSelect
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    version.name.split(" - ").first(),
                    color = if (isSelected) SilverPrimary else Color.White,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 14.sp
                )
                Text(
                    version.name.split(" - ").getOrElse(1) { "" },
                    color = SilverDark,
                    fontSize = 11.sp
                )
            }
            
            if (version.isInstalled) {
                Icon(
                    Icons.Default.CheckCircle,
                    "Installed",
                    tint = SilverPrimary,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Icon(
                    Icons.Default.Download,
                    "Download",
                    tint = SilverDark,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        IconButton(
            onClick = { },
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                icon,
                label,
                tint = SilverPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            label,
            color = SilverDark,
            fontSize = 10.sp
        )
    }
}
