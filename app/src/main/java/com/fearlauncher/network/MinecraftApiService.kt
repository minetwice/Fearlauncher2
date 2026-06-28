package com.fearlauncher.network

import com.fearlauncher.models.MinecraftVersionManifest
import com.fearlauncher.models.VersionDetail
import retrofit2.http.GET
import retrofit2.http.Url

interface MinecraftApiService {
    @GET("mc/game/version_manifest_v2.json")
    suspend fun getVersionManifest(): MinecraftVersionManifest

    @GET
    suspend fun getVersionDetail(@Url url: String): VersionDetail
}
