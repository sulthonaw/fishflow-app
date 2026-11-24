// OrderDesignTokens.kt
// Developer: Sulthon
// Token warna dan helper format lokal untuk layar Order, Cart, Checkout, dan Tracking.
package com.fishflow.app.core.ui.components

import androidx.compose.ui.graphics.Color
import kotlin.math.roundToInt

object OrderDesignTokens {
    val CanvasBg = Color(0xFFF7F9FB)
    val MaritimeDark = Color(0xFF1A3C34)
    val CoastalTeal = Color(0xFF27695B)
    val CoastalTealDark = Color(0xFF2E6F60)
    val ForestDeep = Color(0xFF01261F)
    val MintAccent = Color(0xFFAEF0DD)
    val SageMuted = Color(0xFF83A69C)
    val SageLight = Color(0xFFAACEC3)
    val TextPrimary = Color(0xFF191C1E)
    val TextSecondary = Color(0xFF414846)
    val TextMuted = Color(0xFF717976)
    val SurfaceMuted = Color(0xFFF2F4F6)
    val SurfaceContainer = Color(0xFFECEEF0)
    val SurfaceBorder = Color(0xFFE6E8EA)
    val DividerColor = Color(0xFFE0E3E5)
    val ErrorBg = Color(0xFFFFDAD6)
    val ErrorText = Color(0xFF93000A)
    val WarningDot = Color(0xFFCEA700)
    val WarningText = Color(0xFF735C00)
    val WarningYellow = Color(0xFFFFE083)
    val InfoBlueBg = Color(0xFFDBEAFE)
    val InfoBlueText = Color(0xFF1D4ED8)
    val WaitingAmberBg = Color(0xFFFFF3CD)

    fun formatKg(qty: Double): String {
        val rounded = (qty * 10.0).roundToInt() / 10.0
        return if (rounded % 1.0 == 0.0) {
            rounded.toInt().toString()
        } else {
            rounded.toString()
        }
    }
}
