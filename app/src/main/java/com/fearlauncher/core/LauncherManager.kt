package com.fearlauncher.core

import com.fearlauncher.logic.VersionManager

import android.content.Context
import java.io.File

object LauncherManager {

    fun launchGame(
        context: Context,
        versionId: String,
        username: String,
        maxMemory: Int,
        renderer: String,
        jvmArgs: String
    ): Process? {
        val gameDir = VersionManager.getGameDirectory(context)
        val versionDir = File(VersionManager.getVersionsDirectory(context), versionId)
        val jarFile = File(versionDir, "$versionId.jar")

        if (!jarFile.exists()) return null

        val jreId = when {
            jvmArgs.contains("JRE 18") -> "18"
            jvmArgs.contains("JRE 25") -> "25"
            else -> "21"
        }
        val jrePath = File(context.filesDir, "jre/$jreId/bin/java")

        val command = mutableListOf<String>()
        command.add(jrePath.absolutePath)
        command.add("-Xmx${maxMemory}M")
        command.add("-Djava.library.path=${File(versionDir, "natives").absolutePath}")

        // Add custom JVM args
        jvmArgs.split(" ").filter { it.isNotBlank() }.forEach { command.add(it) }

        command.add("-cp")
        val classpath = buildClasspath(versionDir)
        command.add(classpath)

        command.add("net.minecraft.client.main.Main") // Default main class

        // Game Arguments
        command.add("--username")
        command.add(username)
        command.add("--version")
        command.add(versionId)
        command.add("--gameDir")
        command.add(gameDir.absolutePath)
        command.add("--assetsDir")
        command.add(File(gameDir, "assets").absolutePath)
        command.add("--assetIndex")
        command.add(versionId) // Simplified

        val processBuilder = ProcessBuilder(command)
        processBuilder.directory(gameDir)

        // Renderer Environment Variables
        val env = processBuilder.environment()
        when (renderer) {
            "Holly Renderer" -> env["GALLIUM_DRIVER"] = "zink"
            "Zink" -> env["GALLIUM_DRIVER"] = "zink"
            "GL4ES" -> env["LIBGL_ES"] = "2"
        }

        return try {
            processBuilder.start()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun executeJar(
        context: Context,
        jarFile: File,
        maxMemory: Int,
        jreVersion: String = "21"
    ): Process? {
        val jrePath = File(context.filesDir, "jre/$jreVersion/bin/java")
        val command = listOf(
            jrePath.absolutePath,
            "-Xmx${maxMemory}M",
            "-jar",
            jarFile.absolutePath
        )
        return try {
            ProcessBuilder(command).directory(context.filesDir).start()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun buildClasspath(versionDir: File): String {
        val libsDir = File(versionDir, "libraries")
        val jars = libsDir.walk().filter { it.extension == "jar" }.map { it.absolutePath }.toList()
        val clientJar = File(versionDir, "${versionDir.name}.jar").absolutePath
        return (jars + clientJar).joinToString(File.pathSeparator)
    }
}
