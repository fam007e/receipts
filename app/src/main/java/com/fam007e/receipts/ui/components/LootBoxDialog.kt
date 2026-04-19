package com.fam007e.receipts.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fam007e.receipts.domain.model.LootBoxTier
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun LootBoxDialog(
    userPreferences: com.fam007e.receipts.data.preferences.UserPreferences,
    onDismiss: () -> Unit, 
    onPurchase: (LootBoxTier) -> Unit
) {
    val credits by userPreferences.lootCredits.collectAsState(initial = 0)
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("🎰 Loot Boxes") },
        text = {
            Column {
                Text("Redeem your good vibes to make embarrassing receipts disappear.")
                Text("Your Credits: $credits", fontWeight = androidx.compose.ui.text.font.FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(16.dp))
                LootBoxTier.entries.forEach { tier ->
                    LootBoxRow(tier = tier, onClick = { onPurchase(tier) })
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Never mind") }
        }
    )
}

@Composable
fun LootBoxRow(tier: LootBoxTier, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(tier.emoji, fontSize = 24.sp)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(tier.label, style = MaterialTheme.typography.titleMedium)
                Text(
                    "Deletes ${tier.deletesCount} receipts",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(tier.price, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
        }
    }
}
