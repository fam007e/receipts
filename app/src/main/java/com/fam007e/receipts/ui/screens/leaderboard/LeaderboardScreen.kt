package com.fam007e.receipts.ui.screens.leaderboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fam007e.receipts.domain.repository.PersonRepository
import com.fam007e.receipts.domain.repository.ReceiptRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@Serializable
data class GroupMember(
    val displayName: String,
    val totalReceipts: Int,
    val worstCategory: String,
    val streak: Int,
    val emoji: String
)

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val personRepository: PersonRepository,
    private val receiptRepository: ReceiptRepository
) : ViewModel() {
    private val _members = MutableStateFlow<List<GroupMember>>(emptyList())
    val members: StateFlow<List<GroupMember>> = _members

    fun loadLocalMembers() {
        viewModelScope.launch {
            personRepository.getAllPersons().collect { persons ->
                val memberList = mutableListOf<GroupMember>()
                for (person in persons) {
                    val stats = receiptRepository.getTopCategory(person.id)
                    val totalCount = receiptRepository.getTotalCount(person.id)
                    memberList.add(GroupMember(
                        displayName = person.name,
                        totalReceipts = totalCount,
                        worstCategory = stats?.name ?: "None",
                        streak = 0,
                        emoji = "👤"
                    ))
                }
                _members.value = memberList
            }
        }
    }

    fun importMember(json: String) {
        try {
            val member = Json.decodeFromString<GroupMember>(json)
            _members.value = _members.value + member
        } catch (e: Exception) {
            // handle error
        }
    }

    fun generateMyProfileJson(member: GroupMember): String {
        return Json.encodeToString(member)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    onBack: () -> Unit,
    viewModel: LeaderboardViewModel = hiltViewModel()
) {
    val members by viewModel.members.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    var showImportDialog by remember { mutableStateOf(false) }
    var importText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadLocalMembers()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("🏆 Who's the Worst?") },
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
                .padding(16.dp)
        ) {
            Text(
                "Lower is better. Allegedly.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(members.sortedByDescending { it.totalReceipts }) { index, member ->
                    LeaderboardRow(rank = index + 1, member = member)
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { showImportDialog = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("📥 Import Friend")
                }
                OutlinedButton(
                    onClick = {
                        val me = members.firstOrNull() ?: return@OutlinedButton
                        val json = viewModel.generateMyProfileJson(me)
                        clipboardManager.setText(AnnotatedString(json))
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Export Me")
                }
            }
        }
    }

    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text("Import Friend") },
            text = {
                TextField(
                    value = importText,
                    onValueChange = { importText = it },
                    placeholder = { Text("Paste friend's evidence code") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.importMember(importText)
                    showImportDialog = false
                    importText = ""
                }) {
                    Text("Import")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun LeaderboardRow(rank: Int, member: GroupMember) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (rank == 1) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = rank.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(40.dp)
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(member.displayName, style = MaterialTheme.typography.titleMedium)
                Text(
                    "Worst: ${member.worstCategory}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    member.totalReceipts.toString(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black
                )
                Text("RECEIPTS", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
