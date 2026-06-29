package com.fearlauncher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fearlauncher.logic.VersionManager
import com.fearlauncher.ui.theme.*
import java.util.Locale

@Composable
fun DownloadStatusBar(status: VersionManager.DownloadStatus) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BlackSurface.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                status.fileName,
                color = SilverPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                "${(status.progress * 100).toInt()}%",
                color = SilverPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = status.progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = SilverPrimary,
            trackColor = SilverDark.copy(alpha = 0.3f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    String.format(Locale.US, "Speed: %.2f MB/s", status.speedMBs),
                    color = SilverDark,
                    fontSize = 10.sp
                )
                Text(
                    String.format(Locale.US, "Size: %.1f / %.1f MB", status.downloadedMB, status.totalMB),
                    color = SilverDark,
                    fontSize = 10.sp
                )
            }
            Text(
                "ETA: ${formatETA(status.etaSeconds)}",
                color = SilverAccent,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private fun formatETA(seconds: Long): String {
    if (seconds <= 0) return "Calculating..."
    val mins = seconds / 60
    val secs = seconds % 60
    return if (mins > 0) "${mins}m ${secs}s" else "${secs}s"
}
