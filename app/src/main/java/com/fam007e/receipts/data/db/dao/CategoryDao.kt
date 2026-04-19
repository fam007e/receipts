package com.fam007e.receipts.data.db.dao

import androidx.room.*
import com.fam007e.receipts.data.db.entities.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories WHERE personId = :personId ORDER BY createdAt DESC")
    fun getCategoriesForPerson(personId: Long): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: Long): CategoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity): Long

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Query("UPDATE categories SET totalCount = 0 WHERE id = :categoryId")
    suspend fun resetThresholdCount(categoryId: Long)

    @Query("SELECT * FROM categories WHERE personId = :personId ORDER BY (SELECT COUNT(*) FROM receipts WHERE categoryId = categories.id) DESC LIMIT 1")
    suspend fun getTopCategoryForPerson(personId: Long): CategoryEntity?
}
