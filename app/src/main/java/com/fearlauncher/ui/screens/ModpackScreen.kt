@file:OptIn(ExperimentalMaterial3Api::class)
package com.fearlauncher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fearlauncher.network.NetworkModule
import com.fearlauncher.network.modrinth.ModrinthProject
import com.fearlauncher.ui.theme.*

@Composable
fun ModpackScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var selectedPlatform by remember { mutableStateOf("Modrinth") }
    var selectedVersion by remember { mutableStateOf("1.20.1") }
    var selectedLoader by remember { mutableStateOf("Fabric") }
    var modpacks by remember { mutableStateOf<List<ModrinthProject>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(searchQuery, selectedVersion, selectedLoader) {
        if (searchQuery.length >= 3) {
            isLoading = true
            try {
                val response = NetworkModule.modrinthApi.searchProjects(searchQuery)
                modpacks = response.hits
            } catch (e: Exception) {
                // Handle error
            } finally {
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "MODPACKS",
                    style = MaterialTheme.typography.headlineMedium,
                    color = SilverAccent,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Powered by $selectedPlatform",
                    color = SilverDark,
                    fontSize = 14.sp
                )
            }
            // Platform Logo Placeholder
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(DeepBlack, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (selectedPlatform == "Modrinth") "M" else "C",
                    color = SilverPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filters
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(label = selectedPlatform, onClick = { selectedPlatform = if (selectedPlatform == "Modrinth") "Curse" else "Modrinth" })
            FilterChip(label = selectedVersion, onClick = {
                selectedVersion = when(selectedVersion) {
                    "1.20.1" -> "1.19.2"
                    "1.19.2" -> "1.18.2"
                    else -> "1.20.1"
                }
            })
            FilterChip(label = selectedLoader, onClick = {
                selectedLoader = when(selectedLoader) {
                    "Fabric" -> "Forge"
                    "Forge" -> "NeoForge"
                    "NeoForge" -> "Quilt"
                    else -> "Fabric"
                }
            })
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search modpacks...", color = SilverDark) },
            leadingIcon = { Icon(Icons.Default.Search, "Search", tint = SilverPrimary) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SilverPrimary,
                unfocusedBorderColor = SilverDark.copy(alpha = 0.5f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = SilverPrimary)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(modpacks) { modpack ->
                    ModpackCard(modpack)
                }
            }
        }
    }
}

@Composable
fun FilterChip(label: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = DeepBlack,
        border = androidx.compose.foundation.BorderStroke(1.dp, SilverDark.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, color = SilverPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(androidx.compose.material.icons.Icons.Default.ArrowDropDown, null, tint = SilverDark, modifier = Modifier.size(14.dp))
        }
    }
}

@Composable
fun ModpackCard(project: ModrinthProject) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BlackSurface.copy(alpha = 0.8f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon placeholder
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(DeepBlack, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Download, "Icon", tint = SilverDark)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(project.title, color = SilverPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    project.description,
                    color = SilverDark,
                    fontSize = 12.sp,
                    maxLines = 2
                )
            }
            IconButton(onClick = {}) {
                Icon(Icons.Default.Download, "Download", tint = SilverPrimary)
            }
        }
    }
}
