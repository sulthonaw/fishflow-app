package com.fishflow.app.feature.tourist.destination.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fishflow.app.data.model.Activity
import com.fishflow.app.feature.tourist.budget.formatRupiah

// ActivityChecklistItem.kt
// Developer: Fatih
// Satu baris aktivitas wisata di detail destinasi.
// Tampilkan: nama aktivitas, biaya (atau "Belum tersedia"), checkbox.
// Stateless - terima Activity dan onCheck lambda.
@Composable
fun ActivityChecklistItem(
    activity: Activity,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onCheckedChange(!checked) }
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = activity.nama,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = activity.kategori,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = if (activity.hasKnownCost) {
                formatRupiah(activity.biaya ?: 0.0)
            } else {
                "Belum tersedia"
            },
            style = MaterialTheme.typography.labelLarge,
            color = if (activity.hasKnownCost) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.error
            }
        )
    }
}
