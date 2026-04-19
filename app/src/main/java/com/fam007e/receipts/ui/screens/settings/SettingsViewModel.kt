package com.fam007e.receipts.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fam007e.receipts.data.preferences.UserPreferences
import com.fam007e.receipts.worker.UpdateManager
import com.fam007e.receipts.worker.UpdateResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val updateManager: UpdateManager
) : ViewModel() {

    val appMode: StateFlow<String> = userPreferences.appMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "receipts")

    val emailEnabled: StateFlow<Boolean> = userPreferences.emailEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val geminiApiKey: StateFlow<String> = userPreferences.geminiApiKey
        .map { it ?: "" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val aiBaseUrl: StateFlow<String> = userPreferences.aiBaseUrl
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "https://generativelanguage.googleapis.com/v1beta/openai/")

    val updateChannel: StateFlow<String> = userPreferences.updateChannel
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "stable")

    private val _updateResult = MutableStateFlow<UpdateResult?>(null)
    val updateResult: StateFlow<UpdateResult?> = _updateResult

    fun setMode(mode: String) {
        viewModelScope.launch {
            userPreferences.setMode(mode)
        }
    }

    fun setEmailEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setEmailEnabled(enabled)
        }
    }

    fun setGeminiApiKey(key: String) {
        viewModelScope.launch {
            userPreferences.setGeminiApiKey(key)
        }
    }

    fun setAiBaseUrl(url: String) {
        viewModelScope.launch {
            userPreferences.setAiBaseUrl(url)
        }
    }

    fun setUpdateChannel(channel: String) {
        viewModelScope.launch {
            userPreferences.setUpdateChannel(channel)
        }
    }

    fun checkForUpdates() {
        viewModelScope.launch {
            _updateResult.value = updateManager.checkForUpdates()
        }
    }
}
