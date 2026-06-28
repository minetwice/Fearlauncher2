package com.fearlauncher.models

import com.google.gson.annotations.SerializedName

data class MinecraftVersionManifest(
    @SerializedName("latest") val latest: Latest,
    @SerializedName("versions") val versions: List<Version>
)

data class Latest(
    @SerializedName("release") val release: String,
    @SerializedName("snapshot") val snapshot: String
)

data class Version(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String,
    @SerializedName("url") val url: String,
    @SerializedName("time") val time: String,
    @SerializedName("releaseTime") val releaseTime: String
)

data class FabricVersion(
    @SerializedName("version") val version: String,
    @SerializedName("stable") val stable: Boolean
)

data class VersionDetail(
    @SerializedName("downloads") val downloads: Downloads
)

data class Downloads(
    @SerializedName("client") val client: DownloadInfo
)

data class DownloadInfo(
    @SerializedName("url") val url: String,
    @SerializedName("size") val size: Long,
    @SerializedName("sha1") val sha1: String
)
