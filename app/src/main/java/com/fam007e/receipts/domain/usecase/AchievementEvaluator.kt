package com.fam007e.receipts.domain.usecase

import com.fam007e.receipts.data.db.dao.AchievementDao
import com.fam007e.receipts.data.db.dao.PersonDao
import com.fam007e.receipts.data.db.dao.ReceiptDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AchievementEvaluator @Inject constructor(
    private val receiptDao: ReceiptDao,
    private val achievementDao: AchievementDao,
    private val personDao: PersonDao
) {
    suspend fun evaluate(personId: Long) {
        val totalCount = receiptDao.getTotalCount(personId)
        val streak     = receiptDao.getCurrentStreak(personId)
        val positives  = receiptDao.getPositiveCount(personId)
        val personCount = personDao.countPersons()

        val toUnlock = mutableListOf<String>()

        // Evidence Thresholds
        if (totalCount >= 1) toUnlock   += "first_receipt"
        if (totalCount >= 5) toUnlock   += "five_receipts"
        if (totalCount >= 50) toUnlock  += "fifty_receipts"
        if (totalCount >= 100) toUnlock += "century"

        // Category Repeats
        val topCategoryId = receiptDao.getTopCategoryId(personId)
        if (topCategoryId != null) {
            val topCount = receiptDao.getCategoryCount(personId, topCategoryId)
            if (topCount >= 5) toUnlock  += "repeat_5"
            if (topCount >= 10) toUnlock += "repeat_10"
            if (topCount >= 25) toUnlock += "repeat_25"
        }

        // Streaks
        if (streak >= 2) toUnlock  += "streak_2"
        if (streak >= 7) toUnlock  += "streak_7"
        if (streak >= 30) toUnlock += "streak_30"

        // Positives
        if (positives >= 1) toUnlock  += "first_positive"
        if (positives >= 10) toUnlock += "ten_positives"

        // Multi-Person
        if (personCount >= 3) toUnlock += "three_people"
        if (personCount >= 5) toUnlock += "five_people"

        toUnlock.forEach { id ->
            unlock(id)
        }
    }

    suspend fun unlock(id: String) {
        if (!achievementDao.isUnlocked(id)) {
            achievementDao.unlock(id)
        }
    }
}
