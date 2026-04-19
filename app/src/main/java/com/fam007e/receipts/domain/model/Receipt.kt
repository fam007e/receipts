package com.fam007e.receipts.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Receipt(
    val id: Long = 0,
    val personId: Long,
    val categoryId: Long?,
    val mediaPath: String,
    val mediaType: String,
    val thumbnailPath: String? = null,
    val note: String = "",
    val isPositive: Boolean = false,
    val isHidden: Boolean = false,
    val lootBoxUsed: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val latitude: Double? = null,
    val longitude: Double? = null
)
