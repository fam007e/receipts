package com.fam007e.receipts.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(onNavigateToCapture: () -> Unit) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCapture) {
                Text("📸")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            Text("Your Receipts Feed", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            Text("No receipts yet. Start by snapping one!")
        }
    }
}
