package com.fam007e.receipts.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DivorceLawyerReferralBanner() {
    val uriHandler = LocalUriHandler.current
    
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3CD)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("⚖️", fontSize = 28.sp)
            Spacer(Modifier.width(8.dp))
            Column {
                Text("Having second thoughts?", fontWeight = FontWeight.Bold)
                Text(
                    "Find a family law attorney near you.",
                    style = MaterialTheme.typography.bodySmall
                )
                TextButton(onClick = { 
                    uriHandler.openUri("https://www.google.com/search?q=family+law+attorney+near+me")
                }) {
                    Text("Find attorneys in your area →")
                }
            }
        }
    }
}
