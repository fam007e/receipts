package com.fam007e.receipts.ui.screens.feed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fam007e.receipts.domain.model.Receipt
import com.fam007e.receipts.domain.model.Person
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    personId: Long,
    onNavigateToExpose: () -> Unit,
    onNavigateToCoaching: (Long) -> Unit,
    viewModel: FeedViewModel = hiltViewModel()
) {
    val receipts by viewModel.receipts.collectAsStateWithLifecycle()
    val person by viewModel.person.collectAsStateWithLifecycle()
    val advice by viewModel.coachingAdvice.collectAsStateWithLifecycle()
    val unoReverseResult by viewModel.unoReverseResult.collectAsStateWithLifecycle()

    LaunchedEffect(personId) {
        viewModel.load(personId)
    }

    Scaffold(
        topBar = {
            person?.let { p ->
                CenterAlignedTopAppBar(
                    title = { Text("${p.name}'s Receipts") },
                    actions = {
                        IconButton(onClick = { viewModel.checkUnoReverse(5) }) {
                            Text("🔄")
                        }
                        IconButton(onClick = onNavigateToExpose) {
                            Text("💥")
                        }
                    }
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            advice?.let { text ->
                item {
                    CoachingCard(advice = text)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            items(receipts, key = { it.id }) { receipt ->
                ReceiptCard(
                    receipt = receipt,
                    onGetCoaching = { onNavigateToCoaching(receipt.id) }
                )
            }
        }
    }

    unoReverseResult?.let { res ->
        if (res is com.fam007e.receipts.domain.usecase.UnoReverseResult.CanReverse) {
            com.fam007e.receipts.ui.components.UnoReverseDialog(
                result = res,
                onReverse = { viewModel.clearUnoReverse() },
                onDismiss = { viewModel.clearUnoReverse() }
            )
        }
    }
}

@Composable
fun ReceiptCard(receipt: Receipt, onGetCoaching: () -> Unit) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = if (receipt.isPositive) "💛 Positive" else "📸 Receipt",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (receipt.isPositive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
                TextButton(onClick = onGetCoaching) {
                    Text("Coach Context", style = MaterialTheme.typography.labelSmall)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (receipt.note.isNotEmpty()) {
                Text(text = receipt.note, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text(
                text = dateFormat.format(Date(receipt.timestamp)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun CoachingCard(advice: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "🌱 Relationship Coach",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = advice,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}
