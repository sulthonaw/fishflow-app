// StatusBadge.kt
// Developer: Sulthon
// Badge kecil yang menampilkan status pesanan dengan warna berbeda.
// MENUNGGU = kuning, DIKONFIRMASI = biru, SIAP_DIAMBIL = hijau, SELESAI = abu, DITOLAK = merah.
package com.fishflow.app.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fishflow.app.data.model.OrderStatus

@Composable
fun StatusBadge(
    status: OrderStatus,
    modifier: Modifier = Modifier,
    showIcon: Boolean = true
) {
    val (bgColor, contentColor, dotColor) = when (status) {
        OrderStatus.MENUNGGU_KONFIRMASI -> Triple(
            OrderDesignTokens.WaitingAmberBg,
            OrderDesignTokens.WarningText,
            OrderDesignTokens.WarningDot
        )
        OrderStatus.DIKONFIRMASI -> Triple(
            OrderDesignTokens.InfoBlueBg,
            OrderDesignTokens.InfoBlueText,
            OrderDesignTokens.InfoBlueText
        )
        OrderStatus.SIAP_DIAMBIL -> Triple(
            OrderDesignTokens.MintAccent,
            OrderDesignTokens.CoastalTealDark,
            OrderDesignTokens.CoastalTeal
        )
        OrderStatus.SELESAI -> Triple(
            OrderDesignTokens.SurfaceContainer,
            OrderDesignTokens.TextSecondary,
            OrderDesignTokens.TextMuted
        )
        OrderStatus.DITOLAK,
        OrderStatus.DIBATALKAN -> Triple(
            OrderDesignTokens.ErrorBg,
            OrderDesignTokens.ErrorText,
            OrderDesignTokens.ErrorText
        )
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(9999.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (showIcon) {
            when (status) {
                OrderStatus.SIAP_DIAMBIL -> Icon(
                    imageVector = Icons.Rounded.Schedule,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(12.dp)
                )
                OrderStatus.SELESAI,
                OrderStatus.DIKONFIRMASI -> Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(12.dp)
                )
                else -> Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(dotColor)
                )
            }
        }
        Text(
            text = status.label,
            color = contentColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.12.sp
        )
    }
}
