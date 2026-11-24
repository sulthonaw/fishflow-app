// SellerInfoBadge.kt
// Developer: Octavia
// Badge kecil info penjual di kartu/detail produk.
// Tampilkan: nama penjual/nelayan, nama kapal. Stateless.
package com.fishflow.app.feature.tourist.catalog.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DirectionsBoat
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fishflow.app.core.ui.components.OrderDesignTokens

@Composable
fun SellerInfoBadge(
    namaPenjual: String,
    namaKapal: String?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.DirectionsBoat,
            contentDescription = "Penjual",
            tint = OrderDesignTokens.CoastalTeal,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = if (namaKapal.isNullOrBlank()) namaPenjual else "$namaPenjual • $namaKapal",
            style = MaterialTheme.typography.bodySmall,
            color = OrderDesignTokens.TextSecondary
        )
    }
}
