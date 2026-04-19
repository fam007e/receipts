package com.fam007e.receipts.data.db.dao

import androidx.room.*
import com.fam007e.receipts.data.db.entities.ReceiptEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReceiptDao {
    @Query("SELECT * FROM receipts WHERE personId = :personId")
    fun getReceiptsForPerson(personId: Long): Flow<List<ReceiptEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceipt(receipt: ReceiptEntity): Long

    @Query("UPDATE receipts SET isHidden = 1, lootBoxUsed = 1 WHERE id = :id")
    suspend fun markHidden(id: Long)

    @Query("SELECT COUNT(*) FROM receipts WHERE personId = :personId")
    suspend fun getTotalCount(personId: Long): Int

    @Query("SELECT categoryId FROM receipts WHERE personId = :personId GROUP BY categoryId ORDER BY COUNT(*) DESC LIMIT 1")
    suspend fun getTopCategoryId(personId: Long): Long?

    @Query("SELECT * FROM receipts WHERE id = :id")
    suspend fun getReceiptById(id: Long): ReceiptEntity?

    @Query("SELECT COUNT(*) FROM receipts WHERE personId = :personId AND categoryId = :categoryId")
    suspend fun getCategoryCount(personId: Long, categoryId: Long): Int

    @Query("SELECT COUNT(*) FROM (SELECT DISTINCT timestamp / 86400000 FROM receipts WHERE personId = :personId)")
    suspend fun getCurrentStreak(personId: Long): Int

    @Query("SELECT COUNT(*) FROM receipts WHERE personId = :personId AND isPositive = 1")
    suspend fun getPositiveCount(personId: Long): Int

    @Query("SELECT * FROM receipts WHERE personId = :personId AND categoryId = :categoryId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestInCategory(personId: Long, categoryId: Long): ReceiptEntity?

    @Query("SELECT * FROM receipts WHERE personId = :personId AND categoryId = :categoryId ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentsInCategory(personId: Long, categoryId: Long, limit: Int): List<ReceiptEntity>

    @Query("SELECT * FROM receipts WHERE personId = :personId AND isHidden = 0 ORDER BY (SELECT COUNT(*) FROM receipts r2 WHERE r2.categoryId = receipts.categoryId) DESC LIMIT :limit")
    suspend fun getMostEmbarrassingReceipts(personId: Long, limit: Int): List<ReceiptEntity>
}
