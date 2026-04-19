package com.fam007e.receipts.domain.repository

import com.fam007e.receipts.domain.model.Receipt
import kotlinx.coroutines.flow.Flow

interface ReceiptRepository {
    fun getReceiptsForPerson(personId: Long): Flow<List<Receipt>>
    suspend fun insertReceipt(receipt: Receipt): Long
    suspend fun markHidden(id: Long)
    suspend fun getTotalCount(personId: Long): Int
    suspend fun getTopCategoryId(personId: Long): Long?
}
