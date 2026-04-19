package com.fam007e.receipts.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LittlesColorScheme = lightColorScheme(
    primary = Color(0xFF7EC8A4),           // Sage green
    onPrimary = Color.White,
    secondary = Color(0xFFFFB6B9),         // Soft blush pink
    background = Color(0xFFF9F6F0),        // Warm cream
    surface = Color(0xFFFFFFFF),
    onBackground = Color(0xFF3D3D3D),
    tertiary = Color(0xFFA8D8EA),          // Sky blue
)

@Composable
fun LittlesTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LittlesColorScheme,
        typography = Typography,
        content = content
    )
}
