package com.fam007e.receipts.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: Long = 0,
    val personId: Long,
    val name: String,
    val emoji: String,
    val threshold: Int = 10,
    val isPositive: Boolean = false,
    val totalCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
