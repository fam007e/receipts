package com.fam007e.receipts.ui.screens.expose

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fam007e.receipts.domain.usecase.ExposeEligibility
import com.fam007e.receipts.domain.usecase.TriggerExposeUseCase
import com.fam007e.receipts.worker.ExposeVideoBuilder
import com.fam007e.receipts.domain.repository.ReceiptRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExposeViewModel @Inject constructor(
    private val triggerExposeUseCase: TriggerExposeUseCase,
    private val receiptRepository: ReceiptRepository,
    private val exposeVideoBuilder: ExposeVideoBuilder,
    private val userPreferences: com.fam007e.receipts.data.preferences.UserPreferences,
    private val achievementEvaluator: com.fam007e.receipts.domain.usecase.AchievementEvaluator
) : ViewModel() {
    private val _eligibility = MutableStateFlow<ExposeEligibility>(ExposeEligibility.NotYet(0, 50))
    val eligibility: StateFlow<ExposeEligibility> = _eligibility

    private val _videoPath = MutableStateFlow<String?>(null)
    val videoPath: StateFlow<String?> = _videoPath

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating

    fun checkEligibility(personId: Long) {
        viewModelScope.launch {
            _eligibility.value = triggerExposeUseCase.canExpose(personId)
        }
    }

    fun buildVideo(personId: Long) {
        viewModelScope.launch {
            val status = _eligibility.value
            if (status is ExposeEligibility.Ready) {
                _isGenerating.value = true
                val receipts = receiptRepository.getRecentsInCategory(personId, status.category.id, 20)
                val path = exposeVideoBuilder.buildExposeVideo(status.category.name, receipts)
                _videoPath.value = path
                _isGenerating.value = false
                
                if (path != null) {
                    achievementEvaluator.unlock("first_expose")
                    userPreferences.setLastExposeTime(System.currentTimeMillis())
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposeScreen(
    personId: Long,
    onBack: () -> Unit,
    viewModel: ExposeViewModel = hiltViewModel()
) {
    val eligibility by viewModel.eligibility.collectAsState()
    val videoPath by viewModel.videoPath.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()

    LaunchedEffect(personId) {
        viewModel.checkEligibility(personId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("💥 Expose") },
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
            verticalArrangement = Arrangement.Center
        ) {
            when (val e = eligibility) {
                is ExposeEligibility.OnCooldown -> {
                    Text("Expose on Cooldown", style = MaterialTheme.typography.headlineSmall, color = Color.Gray)
                    Spacer(Modifier.height(8.dp))
                    Text("The investigation needs time to breathe.")
                    Text("Next available in: ${e.daysRemaining} days")
                }
                is ExposeEligibility.NotYet -> {
                    Text("Evidence status: Building a Case", style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(8.dp))
                    Text("${e.needed - e.current} more receipts until you can expose them.")
                    Spacer(Modifier.height(16.dp))
                    LinearProgressIndicator(
                        progress = { e.current.toFloat() / e.needed },
                        modifier = Modifier.fillMaxWidth().height(8.dp)
                    )
                }
                is ExposeEligibility.Ready -> {
                    Text("READY TO EXPOSE", style = MaterialTheme.typography.headlineMedium, color = Color.Red)
                    Text("You've caught them in the act for '${e.category.name}' ${e.category.totalCount} times.")
                    Spacer(Modifier.height(24.dp))
                    
                    if (isGenerating) {
                        CircularProgressIndicator()
                        Text("Compiling shame video...")
                    } else if (videoPath != null) {
                        Text("Video generated successfully!", color = Color.Green)
                        Text("File: $videoPath", style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(16.dp))
                        val context = androidx.compose.ui.platform.LocalContext.current
                        Button(onClick = { 
                            try {
                                val file = java.io.File(videoPath!!)
                                val uri = androidx.core.content.FileProvider.getUriForFile(
                                    context,
                                    "${context.packageName}.fileprovider",
                                    file
                                )
                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                                    setDataAndType(uri, "video/mp4")
                                    addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                // handle error
                            }
                        }) {
                            Text("Play Video")
                        }
                    } else {
                        Button(
                            onClick = { viewModel.buildVideo(personId) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text("🎬 GENERATE EXPOSE VIDEO")
                        }
                    }
                }
            }
            
            Spacer(Modifier.height(32.dp))
            Text(
                "Warning: This feature may impact relationships. Use with extreme caution.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
