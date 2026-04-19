package com.fam007e.receipts.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fam007e.receipts.ui.components.DivorceLawyerReferralBanner
import com.fam007e.receipts.ui.components.LootBoxDialog

@Composable
fun HomeScreen(
    onNavigateToCapture: (Long?) -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToFeed: (Long) -> Unit,
    onNavigateToAchievements: () -> Unit,
    onNavigateToLeaderboard: () -> Unit,
    onNavigateToPremium: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    var showLootBox by remember { mutableStateOf(false) }
    val persons by viewModel.persons.collectAsState()

    Scaffold(
        floatingActionButton = {
            Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                SmallFloatingActionButton(
                    onClick = { showLootBox = true },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text("🎰")
                }
                Spacer(Modifier.height(8.dp))
                FloatingActionButton(onClick = { onNavigateToCapture(null) }) {
                    Text("📸")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Your Receipts Feed", style = MaterialTheme.typography.titleLarge)
                Row {
                    IconButton(onClick = onNavigateToAchievements) { Text("🏆") }
                    IconButton(onClick = onNavigateToSettings) { Text("⚙️") }
                }
            }
            Spacer(Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onNavigateToStats, modifier = Modifier.weight(1f)) {
                    Text("📊 Stats")
                }
                Button(onClick = onNavigateToLeaderboard, modifier = Modifier.weight(1f)) {
                    Text("🌍 Group")
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (persons.isEmpty()) {
                    item {
                        Text("No persons tracked yet. Add one in Settings or Capture a receipt!")
                    }
                } else {
                    items(persons, key = { it.id }) { person ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onNavigateToFeed(person.id) }
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                Text(person.name, style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.weight(1f))
                                Text("View Evidence →", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onNavigateToPremium,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
            ) {
                Text("💎 Support Development")
            }
            Spacer(Modifier.height(16.dp))
            DivorceLawyerReferralBanner()
        }
    }

    if (showLootBox) {
        LootBoxDialog(
            userPreferences = viewModel.userPreferences,
            onDismiss = { showLootBox = false },
            onPurchase = { tier ->
                viewModel.useLootBox(tier)
                showLootBox = false
            }
        )
    }
}
