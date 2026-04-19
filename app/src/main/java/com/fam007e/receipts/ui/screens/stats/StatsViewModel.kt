package com.fam007e.receipts.ui.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fam007e.receipts.data.db.dao.ReceiptDao
import com.fam007e.receipts.domain.model.Person
import com.fam007e.receipts.domain.repository.PersonRepository
import com.fam007e.receipts.domain.repository.ReceiptRepository
import com.fam007e.receipts.domain.model.Receipt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PersonStat(
    val person: Person,
    val totalReceipts: Int,
    val topCategoryName: String,
    val streak: Int
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val personRepository: PersonRepository,
    private val receiptRepository: ReceiptRepository,
    private val receiptDao: ReceiptDao
) : ViewModel() {

    private val _stats = MutableStateFlow<List<PersonStat>>(emptyList())
    val stats: StateFlow<List<PersonStat>> = _stats

    private val _globalTotal = MutableStateFlow(0)
    val globalTotal: StateFlow<Int> = _globalTotal

    private val _allReceipts = MutableStateFlow<List<Receipt>>(emptyList())
    val allReceipts: StateFlow<List<Receipt>> = _allReceipts

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            personRepository.getAllPersons().collect { persons ->
                val statList = mutableListOf<PersonStat>()
                val allRecs = mutableListOf<Receipt>()
                var total = 0
                for (person in persons) {
                    val receipts = receiptRepository.getReceiptsForPerson(person.id).first()
                    allRecs.addAll(receipts)
                    
                    val count  = receiptDao.getTotalCount(person.id)
                    val streak = receiptDao.getCurrentStreak(person.id)
                    val top    = receiptRepository.getTopCategory(person.id)
                    total += count
                    statList += PersonStat(
                        person          = person,
                        totalReceipts   = count,
                        topCategoryName = top?.name ?: "None",
                        streak          = streak
                    )
                }
                _stats.value     = statList.sortedByDescending { it.totalReceipts }
                _globalTotal.value = total
                _allReceipts.value = allRecs
            }
        }
    }
}
