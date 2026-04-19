package com.fam007e.receipts.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val ReceiptsColorScheme = darkColorScheme(
    primary = EvidenceRed,
    secondary = EvidenceYellow,
    tertiary = Color.White,
    background = EvidenceDark,
    surface = EvidenceGray,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
)

private val LittlesColorScheme = lightColorScheme(
    primary = LittlesSage,
    secondary = LittlesBlush,
    tertiary = LittlesSky,
    background = LittlesCream,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color(0xFF3D3D3D),
    onSurface = Color(0xFF3D3D3D),
)

@Composable
fun ReceiptsTheme(
    mode: String = "receipts",
    content: @Composable () -> Unit
) {
    val colorScheme = if (mode == "littles") LittlesColorScheme else ReceiptsColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = (mode == "littles")
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
