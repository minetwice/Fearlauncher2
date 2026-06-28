package com.fearlauncher.logic

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

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
        val jsonFile = File(versionDir, "$versionId.json")
        val jarFile = File(versionDir, "$versionId.jar")
        return jsonFile.exists() && jarFile.exists()
    }

    suspend fun downloadVersion(
        context: Context,
        versionId: String,
        clientJarUrl: String,
        onProgress: (Float) -> Unit
    ) = withContext(Dispatchers.IO) {
        val versionDir = File(getVersionsDirectory(context), versionId)
        if (!versionDir.exists()) versionDir.mkdirs()

        val destination = File(versionDir, "$versionId.jar")

        val client = OkHttpClient()
        val request = Request.Builder().url(clientJarUrl).build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return@withContext

            val body = response.body ?: return@withContext
            val contentLength = body.contentLength()

            body.byteStream().use { input ->
                FileOutputStream(destination).use { output ->
                    val buffer = ByteArray(8192)
                    var bytesRead: Int
                    var totalBytesRead = 0L

                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        totalBytesRead += bytesRead
                        if (contentLength > 0) {
                            onProgress(totalBytesRead.toFloat() / contentLength)
                        }
                    }
                }
            }
        }
    }
}
