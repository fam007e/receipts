package com.fam007e.receipts.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fam007e.receipts.domain.model.Category

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val personId: Long,
    val name: String,
    val emoji: String,
    val threshold: Int = 10,
    val isPositive: Boolean = false,
    val totalCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): Category = Category(
        id = id,
        personId = personId,
        name = name,
        emoji = emoji,
        threshold = threshold,
        isPositive = isPositive,
        totalCount = totalCount,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(category: Category): CategoryEntity = CategoryEntity(
            id = category.id,
            personId = category.personId,
            name = category.name,
            emoji = category.emoji,
            threshold = category.threshold,
            isPositive = category.isPositive,
            totalCount = category.totalCount,
            createdAt = category.createdAt
        )
    }
}
