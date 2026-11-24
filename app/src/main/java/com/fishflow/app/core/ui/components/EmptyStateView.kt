// EmptyStateView.kt
// Developer: Octavia
//
// Koneksi: Empty state sering muncul di katalog ikan (screen Octavia)
// saat tidak ada produk di wilayah yang dipilih.
//
// Tampilan saat data kosong. Terima message, keterangan, dan ikon opsional. Stateless.
package com.fishflow.app.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun EmptyStateView(
    message: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    icon: ImageVector = Icons.Rounded.SearchOff
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = OrderDesignTokens.SageMuted,
            modifier = Modifier.size(48.dp)
        )
        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = OrderDesignTokens.TextPrimary,
            textAlign = TextAlign.Center
        )
        if (description != null) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = OrderDesignTokens.TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}
