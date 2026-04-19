package com.fam007e.receipts.ui.screens.littles

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fam007e.receipts.ai.GeminiCoachingClient
import com.fam007e.receipts.domain.repository.ReceiptRepository
import com.fam007e.receipts.data.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CoachingState {
    object Loading : CoachingState()
    data class Ready(val advice: String) : CoachingState()
    data class Error(val message: String) : CoachingState()
}

@HiltViewModel
class LittlesViewModel @Inject constructor(
    private val receiptRepository: ReceiptRepository,
    private val coachingClient: GeminiCoachingClient,
    private val userPreferences: UserPreferences
) : ViewModel() {
    private val _coachingState = MutableStateFlow<CoachingState>(CoachingState.Loading)
    val coachingState: StateFlow<CoachingState> = _coachingState

    fun getAdvice(receiptId: Long) {
        viewModelScope.launch {
            _coachingState.value = CoachingState.Loading
            
            val countToday = userPreferences.dailyCoachCount.first()
            if (countToday >= 3) {
                _coachingState.value = CoachingState.Error("You've reached your daily limit of 3 advice requests. Reflect on the guidance you've received today.")
                return@launch
            }

            try {
                val receipt = receiptRepository.getReceiptById(receiptId) ?: throw Exception("Receipt not found")
                val category = receipt.categoryId?.let { receiptRepository.getCategoryById(it) }
                val count = if (receipt.categoryId != null) receiptRepository.getCategoryCount(receipt.personId, receipt.categoryId!!) else 0
                
                val advice = coachingClient.getAdvice(
                    categoryName = category?.name ?: "Unknown category",
                    totalCount = count,
                    note = receipt.note
                )
                userPreferences.incrementCoachCount()
                _coachingState.value = CoachingState.Ready(advice)
            } catch (e: Exception) {
                _coachingState.value = CoachingState.Error(e.message ?: "Failed to get advice")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LittlesCoachScreen(
    receiptId: Long,
    onBack: () -> Unit,
    viewModel: LittlesViewModel = hiltViewModel()
) {
    val state by viewModel.coachingState.collectAsState()

    LaunchedEffect(receiptId) {
        viewModel.getAdvice(receiptId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("💧 Let's Talk About This") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                "Little things build up. Let's find a way forward.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(Modifier.height(32.dp))

            when (val s = state) {
                is CoachingState.Loading -> {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(16.dp))
                    Text("Consulting the coach...")
                }
                is CoachingState.Ready -> {
                    CoachAdviceCard(s.advice)
                    Spacer(Modifier.height(32.dp))
                    Button(
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("I'm ready to talk")
                    }
                }
                is CoachingState.Error -> {
                    Text(s.message, color = Color.Gray, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { viewModel.getAdvice(receiptId) }) {
                        Text("Try Again")
                    }
                }
            }
        }
    }
}

@Composable
fun CoachAdviceCard(advice: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF9F6F0) // Warm cream from LittlesTheme
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("🌱 CONSTRUCTIVE PATH", style = MaterialTheme.typography.labelLarge, color = Color(0xFF7EC8A4))
            Spacer(Modifier.height(12.dp))
            Text(
                advice,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp
            )
        }
    }
}
