package com.fam007e.receipts.ui.screens.premium

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import android.app.Activity
import com.fam007e.receipts.billing.BillingManager

import androidx.compose.ui.text.font.FontWeight
import com.fam007e.receipts.ui.components.DivorceLawyerReferralBanner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumScreen(
    billingManager: BillingManager,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Investigation Support") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "This app is Free and Open Source. If you find it helpful for gathering evidence or relationship coaching, please consider supporting the developer.",
                style = MaterialTheme.typography.bodyLarge
            )
            
            val context = LocalContext.current
            
            PremiumCard(
                title = "Support the Dev",
                price = "Donation",
                features = listOf(
                    "Keep the investigation going",
                    "Support FOSS development",
                    "Help pay for AI API costs",
                    "Peace of mind"
                ),
                onSelect = { 
                    (context as? Activity)?.let { activity ->
                        billingManager.launchPremiumFlow(activity, "donation") 
                    }
                }
            )

            Spacer(Modifier.weight(1f))
            
            DivorceLawyerReferralBanner()
        }
    }
}

@Composable
fun PremiumCard(
    title: String,
    price: String,
    badge: String? = null,
    features: List<String>,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onSelect
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                if (badge != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    SuggestionChip(onClick = {}, label = { Text(badge) })
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(price, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))
            
            features.forEach { feature ->
                Text("✅ $feature", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
