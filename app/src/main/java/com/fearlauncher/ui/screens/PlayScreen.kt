package com.fearlauncher.ui.screens

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
import com.fearlauncher.ui.theme.*
import com.fearlauncher.utils.RuntimeManager

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
    var isLoading by remember { mutableStateOf(false) }
    
    val availableVersions = listOf(
        MinecraftVersion("1.20.4", "1.20.4 - Latest Release", "release", true),
        MinecraftVersion("1.20.1", "1.20.1 - Popular SMP", "release", true),
        MinecraftVersion("1.19.2", "1.19.2 - Modded Stable", "release", true),
        MinecraftVersion("1.20.2", "1.20.2 - Snapshot", "snapshot", false),
        MinecraftVersion("1.18.2", "1.18.2 - Caves & Cliffs", "release", false)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(GradientStart, GradientEnd)))
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
            ExposedDropdownMenuBox(
                expanded = false,
                onExpandedChange = {}
            ) {
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.width(200.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = SilverPrimary
                    )
                ) {
                    Icon(Icons.Default.List, "Versions", tint = SilverPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        selectedVersion?.name ?: "Select Version",
                        color = SilverPrimary,
                        fontWeight = FontWeight.Medium
                    )
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
                selectedVersion?.let { version ->
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
                                version.name,
                                style = MaterialTheme.typography.titleLarge,
                                color = SilverPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                version.type,
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
                            
                            Spacer(modifier = Modifier.height(16.dp))

                            // JRE Status
                            val requiredJre = RuntimeManager.getRequiredRuntimeVersion(version.id)
                            val isJreInstalled = RuntimeManager.isRuntimeInstalled(requiredJre)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    if (isJreInstalled) Icons.Default.CheckCircle else Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = if (isJreInstalled) SilverPrimary else Color.Red,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Requires JRE $requiredJre",
                                    color = if (isJreInstalled) SilverDark else Color.Red,
                                    fontSize = 12.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Launch Button
                            val canLaunch = version.isInstalled && isJreInstalled
                            Button(
                                onClick = {
                                    isLoading = true
                                    onLaunchGame(version.id)
                                    isLoading = false
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                enabled = !isLoading,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (canLaunch) SilverPrimary else SilverDark.copy(alpha = 0.5f)
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
                                        Icons.Default.PlayArrow,
                                        "Play",
                                        tint = if (canLaunch) BlackBg else SilverDark,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        when {
                                            !version.isInstalled -> "INSTALL GAME"
                                            !isJreInstalled -> "MISSING JRE $requiredJre"
                                            else -> "LAUNCH GAME"
                                        },
                                        color = if (canLaunch) BlackBg else SilverDark,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }

                            if (version.isInstalled && !isJreInstalled) {
                                TextButton(
                                    onClick = { RuntimeManager.downloadRuntime(requiredJre) {} }
                                ) {
                                    Text("Download JRE $requiredJre", color = SilverPrimary, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                } ?: run {
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
