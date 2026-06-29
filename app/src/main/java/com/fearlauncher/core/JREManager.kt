package com.fearlauncher.core

import android.content.Context
import com.fearlauncher.utils.SystemUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

class JREManager(private val context: Context) {

    enum class JREVersion(val version: Int) {
        JRE_18(18),
        JRE_21(21),
        JRE_25(25)
    }

    fun getJREPath(version: JREVersion): File {
        val jreDir = File(context.filesDir, "jre/${version.version}")
        if (!jreDir.exists()) {
            jreDir.mkdirs()
        }
        return jreDir
    }

    fun isJREInstalled(version: JREVersion): Boolean {
        // Simple check if the directory exists and is not empty
        val path = getJREPath(version)
        return path.exists() && path.list()?.isNotEmpty() == true
    }

    fun getDownloadUrl(version: JREVersion): String {
        val arch = SystemUtils.getArchitecture()
        // Placeholder URLs - in a real app, these would point to actual JRE binaries for Android/Linux
        return when (version) {
            JREVersion.JRE_18 -> "https://example.com/jre18-$arch.tar.gz"
            JREVersion.JRE_21 -> "https://example.com/jre21-$arch.tar.gz"
            JREVersion.JRE_25 -> "https://example.com/jre25-$arch.tar.gz"
        }
    }

    suspend fun downloadJRE(version: JREVersion, onProgress: (Float) -> Unit) = withContext(Dispatchers.IO) {
        val url = getDownloadUrl(version)
        val jreDir = getJREPath(version)
        val destination = File(jreDir, "jre.tar.gz")

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

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
