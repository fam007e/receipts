package com.fam007e.receipts.domain.repository

import com.fam007e.receipts.domain.model.Person
import kotlinx.coroutines.flow.Flow

interface PersonRepository {
    fun getAllPersons(): Flow<List<Person>>
    suspend fun getPersonById(id: Long): Person?
    suspend fun insertPerson(person: Person): Long
    suspend fun updatePerson(person: Person)
    suspend fun deletePerson(person: Person)
}
