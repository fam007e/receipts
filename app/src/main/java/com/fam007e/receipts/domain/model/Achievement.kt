package com.fam007e.receipts.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Achievement(
    val id: String,
    val personId: Long?,
    val name: String,
    val description: String,
    val emoji: String,
    val tier: String,
    val unlockedAt: Long?,
    val isUnlocked: Boolean = false
)

enum class AchievementTier {
    COMMON, RARE, LEGENDARY
}
