// Color.kt
// Developer: Octavia
// Definisi warna Material 3 FishFlow. Octavia yang standardisasi visual keseluruhan app.
//
// Palet memakai OrderDesignTokens (Sulthon) supaya layar yang memakai tema (Fungsi 3)
// dan layar yang memakai token langsung (Fungsi 2 dan 4) tetap satu palet.
package com.fishflow.app.core.ui.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.fishflow.app.core.ui.components.OrderDesignTokens

// Hijau muda dari Figma, belum ada di OrderDesignTokens.
private val MintSurface = Color(0xFFDFF9F1)

// surfaceContainerHighest adalah latar bawaan Card, dibuat putih seperti kartu di Figma.
internal val FishFlowLightColors = lightColorScheme(
    primary = OrderDesignTokens.CoastalTeal,
    onPrimary = Color.White,
    primaryContainer = OrderDesignTokens.MintAccent,
    onPrimaryContainer = OrderDesignTokens.MaritimeDark,
    inversePrimary = OrderDesignTokens.MintAccent,
    secondary = OrderDesignTokens.MaritimeDark,
    onSecondary = Color.White,
    secondaryContainer = MintSurface,
    onSecondaryContainer = OrderDesignTokens.MaritimeDark,
    tertiary = OrderDesignTokens.WarningText,
    onTertiary = Color.White,
    tertiaryContainer = OrderDesignTokens.WarningYellow,
    onTertiaryContainer = OrderDesignTokens.WarningText,
    background = OrderDesignTokens.CanvasBg,
    onBackground = OrderDesignTokens.TextPrimary,
    surface = OrderDesignTokens.CanvasBg,
    onSurface = OrderDesignTokens.TextPrimary,
    surfaceVariant = OrderDesignTokens.SurfaceContainer,
    onSurfaceVariant = OrderDesignTokens.TextSecondary,
    surfaceTint = OrderDesignTokens.CoastalTeal,
    inverseSurface = OrderDesignTokens.MaritimeDark,
    inverseOnSurface = Color.White,
    error = OrderDesignTokens.ErrorText,
    onError = Color.White,
    errorContainer = OrderDesignTokens.ErrorBg,
    onErrorContainer = OrderDesignTokens.ErrorText,
    outline = OrderDesignTokens.TextMuted,
    outlineVariant = OrderDesignTokens.DividerColor,
    surfaceBright = OrderDesignTokens.CanvasBg,
    surfaceDim = OrderDesignTokens.DividerColor,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = OrderDesignTokens.SurfaceMuted,
    surfaceContainer = OrderDesignTokens.SurfaceContainer,
    surfaceContainerHigh = OrderDesignTokens.SurfaceBorder,
    surfaceContainerHighest = Color.White
)
