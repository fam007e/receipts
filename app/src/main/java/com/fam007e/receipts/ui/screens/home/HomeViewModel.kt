package com.fam007e.receipts.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fam007e.receipts.domain.model.Person
import com.fam007e.receipts.domain.repository.PersonRepository
import com.fam007e.receipts.domain.model.LootBoxTier
import com.fam007e.receipts.domain.usecase.UseLootBoxUseCase
import com.fam007e.receipts.data.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val personRepository: PersonRepository,
    private val useLootBoxUseCase: UseLootBoxUseCase,
    val userPreferences: UserPreferences
) : ViewModel() {
    private val _persons = MutableStateFlow<List<Person>>(emptyList())
    val persons: StateFlow<List<Person>> = _persons

    init {
        loadPersons()
    }

    fun loadPersons() {
        viewModelScope.launch {
            personRepository.getAllPersons().collect {
                _persons.value = it
            }
        }
    }

    fun useLootBox(tier: LootBoxTier) {
        viewModelScope.launch {
            // Pick person with most receipts to apply loot box automatically
            // In a more complex app, we'd let the user select. 
            // For now, it targets the most recent tracked person.
            val target = _persons.value.lastOrNull()
            target?.let { 
                useLootBoxUseCase.invoke(tier, it.id)
                loadPersons() // Refresh list
            }
        }
    }
}
