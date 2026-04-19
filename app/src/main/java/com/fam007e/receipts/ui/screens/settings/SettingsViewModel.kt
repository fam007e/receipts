package com.fam007e.receipts.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fam007e.receipts.data.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences
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
}
