package com.fam007e.receipts.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val APP_MODE_KEY = stringPreferencesKey("app_mode")
        private val IS_ONBOARDED_KEY = androidx.datastore.preferences.core.booleanPreferencesKey("is_onboarded")
        private val EMAIL_ENABLED_KEY = androidx.datastore.preferences.core.booleanPreferencesKey("email_enabled")
        private val GEMINI_API_KEY = stringPreferencesKey("gemini_api_key")
        private val AI_BASE_URL = stringPreferencesKey("ai_base_url")
        private val LOOT_CREDITS = androidx.datastore.preferences.core.intPreferencesKey("loot_credits")
        private val LAST_EXPOSE_TIME = androidx.datastore.preferences.core.longPreferencesKey("last_expose_time")
        private val DAILY_COACH_COUNT = androidx.datastore.preferences.core.intPreferencesKey("daily_coach_count")
        private val LAST_COACH_DAY = androidx.datastore.preferences.core.longPreferencesKey("last_coach_day")
    }

    val appMode: Flow<String> = dataStore.data.map { preferences ->
        preferences[APP_MODE_KEY] ?: "receipts"
    }

    val isOnboarded: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[IS_ONBOARDED_KEY] ?: false
    }

    val emailEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[EMAIL_ENABLED_KEY] ?: false
    }

    val geminiApiKey: Flow<String?> = dataStore.data.map { preferences ->
        preferences[GEMINI_API_KEY]
    }

    val aiBaseUrl: Flow<String> = dataStore.data.map { preferences ->
        preferences[AI_BASE_URL] ?: "https://generativelanguage.googleapis.com/v1beta/openai/"
    }

    val lootCredits: Flow<Int> = dataStore.data.map { preferences ->
        preferences[LOOT_CREDITS] ?: 0
    }

    val lastExposeTime: Flow<Long> = dataStore.data.map { preferences ->
        preferences[LAST_EXPOSE_TIME] ?: 0L
    }

    val dailyCoachCount: Flow<Int> = dataStore.data.map { preferences ->
        val lastDay = preferences[LAST_COACH_DAY] ?: 0L
        val today = System.currentTimeMillis() / (24 * 60 * 60 * 1000)
        if (today > lastDay) 0 else preferences[DAILY_COACH_COUNT] ?: 0
    }

    suspend fun incrementCoachCount() {
        dataStore.edit { preferences ->
            val lastDay = preferences[LAST_COACH_DAY] ?: 0L
            val today = System.currentTimeMillis() / (24 * 60 * 60 * 1000)
            if (today > lastDay) {
                preferences[LAST_COACH_DAY] = today
                preferences[DAILY_COACH_COUNT] = 1
            } else {
                val current = preferences[DAILY_COACH_COUNT] ?: 0
                preferences[DAILY_COACH_COUNT] = current + 1
            }
        }
    }

    suspend fun setMode(mode: String) {
        dataStore.edit { preferences ->
            preferences[APP_MODE_KEY] = mode
        }
    }

    suspend fun setOnboarded(onboarded: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_ONBOARDED_KEY] = onboarded
        }
    }

    suspend fun setEmailEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[EMAIL_ENABLED_KEY] = enabled
        }
    }

    suspend fun setGeminiApiKey(key: String) {
        dataStore.edit { preferences ->
            preferences[GEMINI_API_KEY] = key
        }
    }

    suspend fun setAiBaseUrl(url: String) {
        dataStore.edit { preferences ->
            preferences[AI_BASE_URL] = url
        }
    }

    suspend fun addLootCredits(amount: Int) {
        dataStore.edit { preferences ->
            val current = preferences[LOOT_CREDITS] ?: 0
            preferences[LOOT_CREDITS] = current + amount
        }
    }

    suspend fun useLootCredits(amount: Int): Boolean {
        var success = false
        dataStore.edit { preferences ->
            val current = preferences[LOOT_CREDITS] ?: 0
            if (current >= amount) {
                preferences[LOOT_CREDITS] = current - amount
                success = true
            }
        }
        return success
    }

    suspend fun setLastExposeTime(time: Long) {
        dataStore.edit { preferences ->
            preferences[LAST_EXPOSE_TIME] = time
        }
    }
}
