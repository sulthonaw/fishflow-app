// CategoryChips.kt
// Developer: Octavia
// Baris chip kategori (Ikan Segar, Wisata, dll). Stateless.
// Terima List<String> dan onCategoryClick. Chip kategori yang sedang tampil ditandai beda warna.
package com.fishflow.app.feature.tourist.home.components

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.fishflow.app.core.ui.components.HorizontalChipRow
import com.fishflow.app.core.ui.components.OrderDesignTokens

@Composable
fun CategoryChips(
    categories: List<String>,
    selectedCategory: String,
    onCategoryClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    HorizontalChipRow(modifier = modifier) {
        categories.forEach { category ->
            FilterChip(
                selected = category == selectedCategory,
                onClick = { onCategoryClick(category) },
                label = {
                    Text(text = category, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                },
                shape = CircleShape,
                border = null,
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color.White,
                    labelColor = OrderDesignTokens.TextPrimary,
                    selectedContainerColor = OrderDesignTokens.CoastalTeal,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}
