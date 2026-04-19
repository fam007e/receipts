package com.fam007e.receipts.data.db

import com.fam007e.receipts.data.db.dao.AchievementDao
import com.fam007e.receipts.data.db.entities.AchievementEntity
import com.fam007e.receipts.domain.model.AchievementDefinitions
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseSeeder @Inject constructor(
    private val achievementDao: AchievementDao
) {
    suspend fun seedAchievements() {
        val entities = AchievementDefinitions.ALL.map { def ->
            AchievementEntity(
                id          = def.id,
                personId    = null,
                name        = def.name,
                description = def.description,
                emoji       = def.emoji,
                tier        = def.tier,
                unlockedAt  = null,
                isUnlocked  = false
            )
        }
        achievementDao.insertAchievements(entities)
    }
}
