package com.fam007e.receipts.data.db.dao

import androidx.room.*
import com.fam007e.receipts.data.db.entities.PersonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDao {
    @Query("SELECT * FROM persons ORDER BY name ASC")
    fun getAllPersons(): Flow<List<PersonEntity>>

    @Query("SELECT COUNT(*) FROM persons")
    suspend fun countPersons(): Int

    @Query("SELECT * FROM persons WHERE id = :id")
    suspend fun getPersonById(id: Long): PersonEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerson(person: PersonEntity): Long

    @Update
    suspend fun updatePerson(person: PersonEntity)

    @Delete
    suspend fun deletePerson(person: PersonEntity)

    @Query("UPDATE persons SET avatarPath = :avatarPath, autoAvatarCategoryId = :categoryId WHERE id = :personId")
    suspend fun updateAutoAvatar(personId: Long, avatarPath: String?, categoryId: Long?)
}
