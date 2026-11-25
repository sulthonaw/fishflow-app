// Theme.kt
// Developer: Octavia
// FishFlowTheme composable wrapper. Diterapkan di MainActivity oleh Sulthon.
//
// Saat ini baru mengatur warna dan font, hanya mode terang.
package com.fishflow.app.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun FishFlowTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = FishFlowLightColors,
        typography = FishFlowTypography,
        content = content
    )
}
