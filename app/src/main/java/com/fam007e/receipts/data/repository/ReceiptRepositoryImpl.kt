package com.fam007e.receipts.data.repository

import com.fam007e.receipts.data.db.dao.ReceiptDao
import com.fam007e.receipts.data.db.entities.ReceiptEntity
import com.fam007e.receipts.domain.model.Receipt
import com.fam007e.receipts.domain.model.Category
import com.fam007e.receipts.domain.repository.ReceiptRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ReceiptRepositoryImpl @Inject constructor(
    private val receiptDao: ReceiptDao,
    private val categoryDao: com.fam007e.receipts.data.db.dao.CategoryDao
) : ReceiptRepository {
    override fun getReceiptsForPerson(personId: Long): Flow<List<Receipt>> =
        receiptDao.getReceiptsForPerson(personId).map { entities -> entities.map { it.toDomain() } }

    override suspend fun insertReceipt(receipt: Receipt): Long =
        receiptDao.insertReceipt(ReceiptEntity.fromDomain(receipt))
    
    override suspend fun getReceiptById(id: Long): Receipt? =
        receiptDao.getReceiptById(id)?.toDomain()

    override suspend fun markHidden(id: Long) =
        receiptDao.markHidden(id)

    override suspend fun getTotalCount(personId: Long): Int =
        receiptDao.getTotalCount(personId)

    override suspend fun getTopCategoryId(personId: Long): Long? =
        receiptDao.getTopCategoryId(personId)

    override suspend fun getCategoryCount(personId: Long, categoryId: Long): Int =
        receiptDao.getCategoryCount(personId, categoryId)

    override suspend fun getMostEmbarrassingReceipts(personId: Long, limit: Int): List<Receipt> =
        receiptDao.getMostEmbarrassingReceipts(personId, limit).map { it.toDomain() }

    override suspend fun getTopCategory(personId: Long): Category? =
        categoryDao.getTopCategoryForPerson(personId)?.toDomain()

    override suspend fun getCategoryById(categoryId: Long): Category? =
        categoryDao.getCategoryById(categoryId)?.toDomain()

    override suspend fun getRecentsInCategory(personId: Long, categoryId: Long, limit: Int): List<Receipt> =
        receiptDao.getRecentsInCategory(personId, categoryId, limit).map { it.toDomain() }
}
