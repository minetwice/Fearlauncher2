package com.fearlauncher.core

import android.content.Context
import com.fearlauncher.utils.SystemUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.GZIPInputStream
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream

class JREManager(private val context: Context) {

    enum class JREVersion(val version: Int, val id: String) {
        JRE_18(18, "18"),
        JRE_21(21, "21"),
        JRE_25(25, "25")
    }

    fun getJREPath(version: JREVersion): File {
        val jreDir = File(context.filesDir, "jre/${version.id}")
        if (!jreDir.exists()) {
            jreDir.mkdirs()
        }
        return jreDir
    }

    fun isJREInstalled(version: JREVersion): Boolean {
        val binJava = File(getJREPath(version), "bin/java")
        return binJava.exists() && binJava.canExecute()
    }

    fun getDownloadUrl(version: JREVersion): String {
        val arch = SystemUtils.getArchitecture()
        // These should be real URLs for Android-compatible JREs (e.g. from Termux or similar projects)
        return when (version) {
            JREVersion.JRE_18 -> "https://github.com/Adoptium/temurin18-binaries/releases/download/jdk-18.0.2.1%2B1/OpenJDK18U-jdk_aarch64_linux_hotspot_18.0.2.1_1.tar.gz"
            JREVersion.JRE_21 -> "https://github.com/Adoptium/temurin21-binaries/releases/download/jdk-21.0.1%2B12/OpenJDK21U-jdk_aarch64_linux_hotspot_21.0.1_12.tar.gz"
            JREVersion.JRE_25 -> "https://github.com/Adoptium/temurin25-binaries/releases/download/jdk-25/OpenJDK25U-jdk_aarch64_linux_hotspot_25.tar.gz"
        }
    }

    suspend fun downloadAndExtractJRE(version: JREVersion, onProgress: (String, Float) -> Unit) = withContext(Dispatchers.IO) {
        val url = getDownloadUrl(version)
        val jreDir = getJREPath(version)
        val archiveFile = File(jreDir, "jre.tar.gz")

        onProgress("Downloading JRE ${version.version}...", 0f)

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return@withContext
            val body = response.body ?: return@withContext
            val contentLength = body.contentLength()

            body.byteStream().use { input ->
                FileOutputStream(archiveFile).use { output ->
                    val buffer = ByteArray(16384)
                    var bytesRead: Int
                    var totalBytesRead = 0L
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        totalBytesRead += bytesRead
                        if (contentLength > 0) {
                            onProgress("Downloading JRE ${version.version}...", totalBytesRead.toFloat() / contentLength)
                        }
                    }
                }
            }
        }

        onProgress("Extracting JRE ${version.version}...", 1.0f)
        extractTarGz(archiveFile, jreDir)
        archiveFile.delete()

        // Ensure java binary is executable
        File(jreDir, "bin/java").setExecutable(true)
    }

    private fun extractTarGz(archive: File, destination: File) {
        TarArchiveInputStream(GZIPInputStream(FileInputStream(archive))).use { tarInput ->
            var entry = tarInput.nextTarEntry
            while (entry != null) {
                val entryFile = File(destination, entry.name)
                if (entry.isDirectory) {
                    entryFile.mkdirs()
                } else {
                    entryFile.parentFile?.mkdirs()
                    FileOutputStream(entryFile).use { output ->
                        tarInput.copyTo(output)
                    }
                    if (entry.name.contains("bin/")) {
                        entryFile.setExecutable(true)
                    }
                }
                entry = tarInput.nextTarEntry
            }
        }
    }
}
