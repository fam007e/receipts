package com.fam007e.receipts.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "groups")
data class GroupEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val inviteCode: String,
    val createdAt: Long = System.currentTimeMillis()
)
