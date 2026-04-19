package com.fam007e.receipts.domain.usecase

import com.fam007e.receipts.domain.repository.ReceiptRepository
import javax.inject.Inject

sealed class UnoReverseResult {
    data class CanReverse(val reverseCount: Int, val bonusCount: Int) : UnoReverseResult()
    object CannotReverse : UnoReverseResult()
}

class UnoReverseUseCase @Inject constructor(
    private val receiptRepository: ReceiptRepository
) {
    /**
     * Checks if person A can reverse a blast from person B.
     * Logic: If A has >= B's count on B, they can reverse with a +1 bonus.
     */
    suspend fun checkReverse(
        _targetPersonId: Long,
        initiatorPersonId: Long,
        incomingCount: Int
    ): UnoReverseResult {
        val reverseCount = receiptRepository.getTotalCount(initiatorPersonId)

        return if (reverseCount >= incomingCount) {
            UnoReverseResult.CanReverse(
                reverseCount = reverseCount,
                bonusCount = reverseCount - incomingCount + 1
            )
        } else {
            UnoReverseResult.CannotReverse
        }
    }
}
