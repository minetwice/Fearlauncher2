package com.fearlauncher.logic

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

object VersionManager {

    fun getGameDirectory(context: Context): File {
        val dir = File(context.filesDir, ".minecraft")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun getVersionsDirectory(context: Context): File {
        val dir = File(getGameDirectory(context), "versions")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun isVersionInstalled(context: Context, versionId: String): Boolean {
        val versionDir = File(getVersionsDirectory(context), versionId)
        val jarFile = File(versionDir, "$versionId.jar")
        return jarFile.exists() && jarFile.length() > 0
    }

    data class DownloadStatus(
        val fileName: String,
        val progress: Float,
        val downloadedMB: Double,
        val totalMB: Double,
        val speedMBs: Double,
        val etaSeconds: Long
    )

    suspend fun downloadVersion(
        context: Context,
        versionId: String,
        clientJarUrl: String,
        onStatus: (DownloadStatus) -> Unit
    ) = withContext(Dispatchers.IO) {
        val versionDir = File(getVersionsDirectory(context), versionId)
        if (!versionDir.exists()) versionDir.mkdirs()

        val jarFile = File(versionDir, "$versionId.jar")
        downloadFile(clientJarUrl, jarFile) { progress, downloaded, total, speed, eta ->
            onStatus(DownloadStatus(
                fileName = "$versionId.jar",
                progress = progress,
                downloadedMB = downloaded / (1024.0 * 1024.0),
                totalMB = total / (1024.0 * 1024.0),
                speedMBs = speed / (1024.0 * 1024.0),
                etaSeconds = eta
            ))
        }

        // In a complete implementation, this would also download:
        // 1. version.json
        // 2. assets manifest and files
        // 3. libraries (LWJGL, etc.)
        // For now, we simulate success with the JAR.
    }

    private suspend fun downloadFile(
        url: String,
        destination: File,
        onProgress: (progress: Float, downloaded: Long, total: Long, speed: Double, eta: Long) -> Unit
    ) {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return

            val body = response.body ?: return
            val contentLength = body.contentLength()
            val startTime = System.currentTimeMillis()

            body.byteStream().use { input ->
                FileOutputStream(destination).use { output ->
                    val buffer = ByteArray(16384)
                    var bytesRead: Int
                    var totalBytesRead = 0L

                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        totalBytesRead += bytesRead

                        val currentTime = System.currentTimeMillis()
                        val duration = (currentTime - startTime) / 1000.0
                        val speed = if (duration > 0) totalBytesRead / duration else 0.0
                        val remainingBytes = contentLength - totalBytesRead
                        val eta = if (speed > 0) (remainingBytes / speed).toLong() else 0L

                        onProgress(
                            if (contentLength > 0) totalBytesRead.toFloat() / contentLength else 0f,
                            totalBytesRead,
                            contentLength,
                            speed,
                            eta
                        )
                    }
                }
            }
        }
    }
}
