package com.fam007e.receipts.worker

import android.content.Context
import com.fam007e.receipts.data.preferences.UserPreferences
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class GitHubRelease(
    val tag_name: String,
    val prerelease: Boolean,
    val assets: List<GitHubAsset>
)

@Serializable
data class GitHubAsset(
    val name: String,
    val browser_download_url: String
)

sealed class UpdateResult {
    data class NewUpdate(val version: String, val downloadUrl: String) : UpdateResult()
    object NoUpdate : UpdateResult()
    object Error : UpdateResult()
}

@Singleton
class UpdateManager @Inject constructor(
    private val httpClient: HttpClient,
    private val userPreferences: UserPreferences
) {
    private val repoUrl = "https://api.github.com/repos/fam007e/receipts/releases"
    private val currentVersion = com.fam007e.receipts.BuildConfig.VERSION_NAME

    suspend fun checkForUpdates(): UpdateResult {
        return try {
            val channel = userPreferences.updateChannel.first()
            val response = httpClient.get(repoUrl)
            if (response.status.value == 200) {
                val releases = response.body<List<GitHubRelease>>()
                
                val latest = if (channel == "prerelease") {
                    // Get the absolute latest release regardless of status
                    releases.firstOrNull()
                } else {
                    // Get the latest stable release
                    releases.firstOrNull { !it.prerelease }
                }

                if (latest != null && isNewer(latest.tag_name, currentVersion)) {
                    val apkAsset = latest.assets.find { it.name.endsWith(".apk") }
                    if (apkAsset != null) {
                        UpdateResult.NewUpdate(latest.tag_name, apkAsset.browser_download_url)
                    } else {
                        UpdateResult.NoUpdate
                    }
                } else {
                    UpdateResult.NoUpdate
                }
            } else {
                UpdateResult.Error
            }
        } catch (e: Exception) {
            e.printStackTrace()
            UpdateResult.Error
        }
    }

    private fun isNewer(latest: String, current: String): Boolean {
        // Simple version comparison: v1.0.1 vs v1.0.0
        val l = latest.removePrefix("v").split(".").mapNotNull { it.toIntOrNull() }
        val c = current.removePrefix("v").split(".").mapNotNull { it.toIntOrNull() }
        
        for (i in 0 until minOf(l.size, c.size)) {
            if (l[i] > c[i]) return true
            if (l[i] < c[i]) return false
        }
        return l.size > c.size
    }
}
