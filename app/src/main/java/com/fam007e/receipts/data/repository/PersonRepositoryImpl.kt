package com.fam007e.receipts.data.repository

import com.fam007e.receipts.data.db.dao.PersonDao
import com.fam007e.receipts.data.db.entities.PersonEntity
import com.fam007e.receipts.domain.model.Person
import com.fam007e.receipts.domain.repository.PersonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PersonRepositoryImpl @Inject constructor(
    private val personDao: PersonDao
) : PersonRepository {
    override fun getAllPersons(): Flow<List<Person>> =
        personDao.getAllPersons().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getPersonById(id: Long): Person? =
        personDao.getPersonById(id)?.toDomain()

    override suspend fun insertPerson(person: Person): Long =
        personDao.insertPerson(PersonEntity.fromDomain(person))

    override suspend fun updatePerson(person: Person) =
        personDao.updatePerson(PersonEntity.fromDomain(person))

    override suspend fun deletePerson(person: Person) =
        personDao.deletePerson(PersonEntity.fromDomain(person))
}
