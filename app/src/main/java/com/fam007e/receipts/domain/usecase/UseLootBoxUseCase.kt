package com.fam007e.receipts.domain.usecase

import com.fam007e.receipts.data.preferences.UserPreferences
import com.fam007e.receipts.domain.model.LootBoxTier
import com.fam007e.receipts.domain.repository.ReceiptRepository
import javax.inject.Inject
import kotlin.random.Random

class UseLootBoxUseCase @Inject constructor(
    private val receiptRepository: ReceiptRepository,
    private val achievementEvaluator: AchievementEvaluator,
    private val userPreferences: UserPreferences
) {
    /**
     * Executes a loot box purchase.
     * Selects receipts to "disappear" using a pseudo-random selection
     * from the person's most frequent (embarrassing) categories.
     */
    suspend fun invoke(tier: LootBoxTier, targetPersonId: Long): Boolean {
        // Cost Rationale: Basic=1, Rare=3, Legendary=5
        val cost = when(tier) {
            LootBoxTier.BASIC -> 1
            LootBoxTier.RARE -> 3
            LootBoxTier.LEGENDARY -> 5
        }

        if (!userPreferences.useLootCredits(cost)) {
            return false // Not enough positive karma
        }

        // Get a pool of "eligible" embarrassing receipts (e.g., top 20)
        val pool = receiptRepository.getMostEmbarrassingReceipts(targetPersonId, limit = 20)
        
        if (pool.isEmpty()) return false

        // Truly pseudo-randomly pick N receipts from the pool based on tier
        val toDelete = pool.shuffled(Random(System.currentTimeMillis()))
            .take(tier.deletesCount)

        toDelete.forEach { receipt ->
            receiptRepository.markHidden(receipt.id)
        }
        
        // Unlock achievement
        achievementEvaluator.unlock("first_loot")
        if (tier == LootBoxTier.LEGENDARY) {
            achievementEvaluator.unlock("legendary_loot")
        }
        
        return true
    }
}
