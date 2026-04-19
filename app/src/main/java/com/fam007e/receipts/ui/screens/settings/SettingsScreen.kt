package com.fam007e.receipts.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val appMode by viewModel.appMode.collectAsStateWithLifecycle()
    val emailEnabled by viewModel.emailEnabled.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("⚙️ Settings") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            item {
                Text("App Mode", style = MaterialTheme.typography.labelLarge)
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    FilterChip(
                        selected = appMode == "receipts",
                        onClick = { viewModel.setMode("receipts") },
                        label = { Text("Receipts") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterChip(
                        selected = appMode == "littles",
                        onClick = { viewModel.setMode("littles") },
                        label = { Text("Littles") }
                    )
                }
            }
            
            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Threshold Emails")
                        Text("Enable automatic email blasts.", style = MaterialTheme.typography.bodySmall)
                    }
                    Switch(checked = emailEnabled, onCheckedChange = { viewModel.setEmailEnabled(it) })
                }
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                Text("AI Provider Configuration", style = MaterialTheme.typography.labelLarge)
                
                Text("API Key", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
                val apiKey by viewModel.geminiApiKey.collectAsStateWithLifecycle()
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { viewModel.setGeminiApiKey(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("sk-...") },
                    singleLine = true
                )

                Text("Base URL", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
                val baseUrl by viewModel.aiBaseUrl.collectAsStateWithLifecycle()
                OutlinedTextField(
                    value = baseUrl,
                    onValueChange = { viewModel.setAiBaseUrl(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("https://api.openai.com/v1/") },
                    singleLine = true
                )
                Text("Default is Gemini OpenAI endpoint.", style = MaterialTheme.typography.labelSmall)
            }
            
            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                TextButton(onClick = {}) {
                    Text("⚖️ Find a divorce attorney", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
