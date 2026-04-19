package com.fam007e.receipts.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fam007e.receipts.domain.model.Achievement

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String,
    val personId: Long?,
    val name: String,
    val description: String,
    val emoji: String,
    val tier: String,
    val unlockedAt: Long?,
    val isUnlocked: Boolean = false
) {
    fun toDomain(): Achievement = Achievement(
        id = id,
        personId = personId,
        name = name,
        description = description,
        emoji = emoji,
        tier = tier,
        unlockedAt = unlockedAt,
        isUnlocked = isUnlocked
    )

    companion object {
        fun fromDomain(achievement: Achievement): AchievementEntity = AchievementEntity(
            id = achievement.id,
            personId = achievement.personId,
            name = achievement.name,
            description = achievement.description,
            emoji = achievement.emoji,
            tier = achievement.tier,
            unlockedAt = achievement.unlockedAt,
            isUnlocked = achievement.isUnlocked
        )
    }
}
