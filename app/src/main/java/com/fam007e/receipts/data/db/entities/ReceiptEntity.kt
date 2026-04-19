package com.fam007e.receipts.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.ForeignKey.Companion.SET_NULL
import androidx.room.PrimaryKey
import com.fam007e.receipts.domain.model.Receipt

@Entity(
    tableName = "receipts",
    foreignKeys = [
        ForeignKey(
            entity = PersonEntity::class,
            parentColumns = ["id"],
            childColumns = ["personId"],
            onDelete = CASCADE
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = SET_NULL
        )
    ],
    indices = [
        androidx.room.Index("personId"),
        androidx.room.Index("categoryId")
    ]
)
data class ReceiptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val personId: Long,
    val categoryId: Long?,
    val mediaPath: String,
    val mediaType: String,
    val thumbnailPath: String?,
    val note: String = "",
    val isPositive: Boolean = false,
    val isHidden: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val latitude: Double? = null,
    val longitude: Double? = null
) {
    fun toDomain(): Receipt = Receipt(
        id = id,
        personId = personId,
        categoryId = categoryId,
        mediaPath = mediaPath,
        mediaType = mediaType,
        thumbnailPath = thumbnailPath,
        note = note,
        isPositive = isPositive,
        isHidden = isHidden,
        timestamp = timestamp,
        latitude = latitude,
        longitude = longitude
    )

    companion object {
        fun fromDomain(receipt: Receipt): ReceiptEntity = ReceiptEntity(
            id = receipt.id,
            personId = receipt.personId,
            categoryId = receipt.categoryId,
            mediaPath = receipt.mediaPath,
            mediaType = receipt.mediaType,
            thumbnailPath = receipt.thumbnailPath,
            note = receipt.note,
            isPositive = receipt.isPositive,
            isHidden = receipt.isHidden,
            timestamp = receipt.timestamp,
            latitude = receipt.latitude,
            longitude = receipt.longitude
        )
    }
}
