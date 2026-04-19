package com.fam007e.receipts.data.repository

import com.fam007e.receipts.data.db.dao.ReceiptDao
import com.fam007e.receipts.data.db.entities.ReceiptEntity
import com.fam007e.receipts.domain.model.Receipt
import com.fam007e.receipts.domain.repository.ReceiptRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ReceiptRepositoryImpl @Inject constructor(
    private val receiptDao: ReceiptDao
) : ReceiptRepository {
    override fun getReceiptsForPerson(personId: Long): Flow<List<Receipt>> =
        receiptDao.getReceiptsForPerson(personId).map { entities -> entities.map { it.toDomain() } }

    override suspend fun insertReceipt(receipt: Receipt): Long =
        receiptDao.insertReceipt(ReceiptEntity.fromDomain(receipt))

    override suspend fun markHidden(id: Long) =
        receiptDao.markHidden(id)

    override suspend fun getTotalCount(personId: Long): Int =
        receiptDao.getTotalCount(personId)

    override suspend fun getTopCategoryId(personId: Long): Long? =
        receiptDao.getTopCategoryId(personId)
}
