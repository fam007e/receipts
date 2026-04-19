package com.fam007e.receipts.ui.screens.achievements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fam007e.receipts.domain.model.Achievement
import com.fam007e.receipts.data.db.dao.AchievementDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

import kotlinx.coroutines.flow.map

@HiltViewModel
class AchievementsViewModel @Inject constructor(
    private val achievementDao: AchievementDao
) : ViewModel() {
    private val _achievements = MutableStateFlow<List<Achievement>>(emptyList())
    val achievements: StateFlow<List<Achievement>> = _achievements

    init {
        loadAchievements()
    }

    private fun loadAchievements() {
        viewModelScope.launch {
            achievementDao.getAllAchievements()
                .map { entities -> entities.map { it.toDomain() } }
                .collect { _achievements.value = it }
        }
    }
}
