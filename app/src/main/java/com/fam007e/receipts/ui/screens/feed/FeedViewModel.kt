package com.fam007e.receipts.ui.screens.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fam007e.receipts.domain.model.Receipt
import com.fam007e.receipts.domain.model.Person
import com.fam007e.receipts.domain.repository.ReceiptRepository
import com.fam007e.receipts.domain.repository.PersonRepository
import com.fam007e.receipts.ai.GeminiCoachingClient
import com.fam007e.receipts.data.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class FeedViewModel @Inject constructor(
    private val receiptRepository: ReceiptRepository,
    private val personRepository: PersonRepository,
    private val coachingClient: GeminiCoachingClient,
    private val userPreferences: UserPreferences,
    private val unoReverseUseCase: com.fam007e.receipts.domain.usecase.UnoReverseUseCase,
    private val achievementEvaluator: com.fam007e.receipts.domain.usecase.AchievementEvaluator
) : ViewModel() {

    private val _personId = MutableStateFlow<Long?>(null)
    
    private val _unoReverseResult = MutableStateFlow<com.fam007e.receipts.domain.usecase.UnoReverseResult?>(null)
    val unoReverseResult: StateFlow<com.fam007e.receipts.domain.usecase.UnoReverseResult?> = _unoReverseResult

    fun checkUnoReverse(incomingCount: Int) {
        viewModelScope.launch {
            val targetId = _personId.value ?: return@launch
            // Assume the user is 'initiator' (ID 0) for simulation
            val result = unoReverseUseCase.checkReverse(targetId, 0, incomingCount)
            _unoReverseResult.value = result
            if (result is com.fam007e.receipts.domain.usecase.UnoReverseResult.CanReverse) {
                achievementEvaluator.unlock("uno_reverse")
            }
        }
    }

    fun clearUnoReverse() {
        _unoReverseResult.value = null
    }
    val receipts: StateFlow<List<Receipt>> = _personId
        .filterNotNull()
        .flatMapLatest { id -> receiptRepository.getReceiptsForPerson(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val person: StateFlow<Person?> = _personId
        .filterNotNull()
        .mapLatest { id -> personRepository.getPersonById(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val coachingAdvice: StateFlow<String?> = combine(
        _personId.filterNotNull(),
        userPreferences.appMode
    ) { id, mode ->
        if (mode == "littles") {
            val topCategory = receiptRepository.getTopCategory(id)
            if (topCategory != null) {
                val count = receiptRepository.getCategoryCount(id, topCategory.id)
                coachingClient.getAdvice(topCategory.name, count, "")
            } else null
        } else null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun load(personId: Long) {
        _personId.value = personId
    }
}
