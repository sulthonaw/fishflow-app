// SearchBar.kt
// Developer: Octavia
//
// Koneksi: Search bar dipakai di beranda wisatawan dan katalog ikan (screen Octavia).
// Juga dipakai di inventory (Fakhri) dan destinasi (Fatih).
//
// Search bar standar. Terima: query (String), onQueryChange (String) -> Unit.
// Stateless - query dan callback di-hoist ke parent.
// Diberi awalan FishFlow supaya tidak bentrok dengan SearchBar milik Material 3.
package com.fishflow.app.core.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FishFlowSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Cari..."
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Icon(imageVector = Icons.Rounded.Search, contentDescription = null)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Hapus pencarian"
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedTextColor = OrderDesignTokens.TextPrimary,
            unfocusedTextColor = OrderDesignTokens.TextPrimary,
            focusedBorderColor = OrderDesignTokens.CoastalTeal,
            unfocusedBorderColor = OrderDesignTokens.DividerColor,
            cursorColor = OrderDesignTokens.CoastalTeal
        )
    )
}
