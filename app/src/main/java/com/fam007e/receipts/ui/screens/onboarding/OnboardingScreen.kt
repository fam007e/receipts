package com.fam007e.receipts.ui.screens.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingScreen(onComplete: (String) -> Unit) {
    var selectedMode by remember { mutableStateOf("receipts") }
    
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome to Receipts", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        Text("Choose your vibe:")
        
        Row(modifier = Modifier.padding(16.dp)) {
            Button(
                onClick = { selectedMode = "receipts" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedMode == "receipts") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                )
            ) {
                Text("Receipts Mode 📸")
            }
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = { selectedMode = "littles" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedMode == "littles") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                )
            ) {
                Text("Littles Mode 💧")
            }
        }
        
        Spacer(Modifier.height(32.dp))
        
        Button(onClick = { onComplete(selectedMode) }) {
            Text("Let's Start")
        }
    }
}
