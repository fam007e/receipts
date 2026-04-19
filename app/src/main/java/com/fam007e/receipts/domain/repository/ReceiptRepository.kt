package com.fam007e.receipts.domain.repository

import com.fam007e.receipts.domain.model.Receipt
import com.fam007e.receipts.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface ReceiptRepository {
    fun getReceiptsForPerson(personId: Long): Flow<List<Receipt>>
    suspend fun insertReceipt(receipt: Receipt): Long
    suspend fun markHidden(id: Long)
    suspend fun getTotalCount(personId: Long): Int
    suspend fun getTopCategoryId(personId: Long): Long?
    suspend fun getReceiptById(id: Long): Receipt?
    suspend fun getCategoryById(categoryId: Long): Category?
    suspend fun getCategoryCount(personId: Long, categoryId: Long): Int
    suspend fun getMostEmbarrassingReceipts(personId: Long, limit: Int): List<Receipt>
    suspend fun getTopCategory(personId: Long): Category?
    suspend fun getRecentsInCategory(personId: Long, categoryId: Long, limit: Int): List<Receipt>
}
