package com.fam007e.receipts.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Person(
    val id: Long = 0,
    val name: String,
    val relationship: String,
    val avatarPath: String? = null,
    val autoAvatarCategoryId: Long? = null,
    val isMe: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val mode: String = "receipts"
)
