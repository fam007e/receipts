package com.fam007e.receipts.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fam007e.receipts.domain.model.Person

@Entity(tableName = "persons")
data class PersonEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val relationship: String,
    val avatarPath: String?,
    val autoAvatarCategoryId: Long?,
    val isMe: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val mode: String = "receipts"
) {
    fun toDomain(): Person = Person(
        id = id,
        name = name,
        relationship = relationship,
        avatarPath = avatarPath,
        autoAvatarCategoryId = autoAvatarCategoryId,
        isMe = isMe,
        createdAt = createdAt,
        mode = mode
    )

    companion object {
        fun fromDomain(person: Person): PersonEntity = PersonEntity(
            id = person.id,
            name = person.name,
            relationship = person.relationship,
            avatarPath = person.avatarPath,
            autoAvatarCategoryId = person.autoAvatarCategoryId,
            isMe = person.isMe,
            createdAt = person.createdAt,
            mode = person.mode
        )
    }
}
