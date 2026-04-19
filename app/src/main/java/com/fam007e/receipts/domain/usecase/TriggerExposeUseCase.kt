package com.fam007e.receipts.domain.usecase

import com.fam007e.receipts.data.preferences.UserPreferences
import com.fam007e.receipts.domain.repository.ReceiptRepository
import com.fam007e.receipts.domain.model.Category
import kotlinx.coroutines.flow.first
import javax.inject.Inject

sealed class ExposeEligibility {
    data class Ready(val category: Category) : ExposeEligibility()
    data class NotYet(val current: Int, val needed: Int) : ExposeEligibility()
    data class OnCooldown(val daysRemaining: Int) : ExposeEligibility()
}

class TriggerExposeUseCase @Inject constructor(
    private val receiptRepository: ReceiptRepository,
    private val userPreferences: UserPreferences
) {
    suspend fun canExpose(personId: Long): ExposeEligibility {
        // Cooldown check: 30 days
        val lastExpose = userPreferences.lastExposeTime.first()
        val thirtyDaysMillis = 30L * 24 * 60 * 60 * 1000
        val now = System.currentTimeMillis()
        
        if (now - lastExpose < thirtyDaysMillis) {
            val remainingDays = ((thirtyDaysMillis - (now - lastExpose)) / (24 * 60 * 60 * 1000)).toInt()
            return ExposeEligibility.OnCooldown(remainingDays)
        }

        val topCategory = receiptRepository.getTopCategory(personId)
        val needed = 50 // Moral Rationale: 50 receipts represents a serious case.
        
        return if ((topCategory?.totalCount ?: 0) >= needed) {
            ExposeEligibility.Ready(topCategory!!)
        } else {
            ExposeEligibility.NotYet(
                current = topCategory?.totalCount ?: 0,
                needed = needed
            )
        }
    }
}
