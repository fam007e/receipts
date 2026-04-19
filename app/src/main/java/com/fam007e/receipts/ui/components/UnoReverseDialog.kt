package com.fam007e.receipts.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fam007e.receipts.domain.usecase.UnoReverseResult

@Composable
fun UnoReverseDialog(
    result: UnoReverseResult.CanReverse,
    onReverse: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onReverse,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)) // UNO Red
            ) {
                Text("REVERSE!", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Take the hit 😔")
            }
        },
        title = {
            Text(
                "🔄 UNO REVERSE AVAILABLE!",
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFB8C00) // UNO Orange
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "You have ${result.reverseCount} receipts on them!",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF388E3C), RoundedCornerShape(8.dp)) // UNO Green
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "+${result.bonusCount}",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            "BONUS DAMAGE",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                Text("Reversing will fire back all your evidence PLUS a bonus logic blast.", style = MaterialTheme.typography.bodySmall)
            }
        }
    )
}
