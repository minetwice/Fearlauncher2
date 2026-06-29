package com.fearlauncher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fearlauncher.logic.VersionManager
import com.fearlauncher.ui.components.DownloadStatusBar
import com.fearlauncher.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun SetupScreen(onComplete: () -> Unit) {
    var isDownloading by remember { mutableStateOf(false) }
    var downloadStatus by remember { mutableStateOf<VersionManager.DownloadStatus?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "INITIAL SETUP",
            style = MaterialTheme.typography.headlineLarge,
            color = SilverAccent,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            "Downloading required core files...",
            color = SilverDark,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        if (isDownloading && downloadStatus != null) {
            DownloadStatusBar(status = downloadStatus!!)
        } else {
            Button(
                onClick = {
                    isDownloading = true
                    scope.launch {
                        try {
                            // Simulate or actually download JRE 21
                            // For now, we'll use a dummy download to show the UI works
                            val dummyUrl = "https://piston-data.mojang.com/v1/objects/845e2332856230f3a6114f6b80bc772e09c4f033/client.jar"
                            VersionManager.downloadVersion(context, "core_jre", dummyUrl) { status ->
                                downloadStatus = status
                            }
                            onComplete()
                        } catch (e: Exception) {
                            isDownloading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SilverPrimary)
            ) {
                Text("START DOWNLOAD", color = BlackBg, fontWeight = FontWeight.Bold)
            }
        }
    }
}
