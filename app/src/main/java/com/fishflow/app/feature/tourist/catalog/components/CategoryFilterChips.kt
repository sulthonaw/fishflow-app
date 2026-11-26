// CategoryFilterChips.kt
// Developer: Octavia
// Baris chip filter kategori ikan. Stateless.
package com.fishflow.app.feature.tourist.catalog.components

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
import com.fishflow.app.data.model.FishCategory

@Composable
fun CategoryFilterChips(
    categories: List<FishCategory>,
    selectedCategory: FishCategory?,
    onSelect: (FishCategory?) -> Unit,
    modifier: Modifier = Modifier
) {
    HorizontalChipRow(modifier = modifier) {
        CategoryChip(
            text = "Semua Kategori",
            isSelected = selectedCategory == null,
            onClick = { onSelect(null) }
        )
        categories.forEach { category ->
            CategoryChip(
                text = category.label,
                isSelected = selectedCategory == category,
                onClick = { onSelect(category) }
            )
        }
    }
}

@Composable
private fun CategoryChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(text = text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        },
        shape = CircleShape,
        border = null,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = OrderDesignTokens.SurfaceContainer,
            labelColor = OrderDesignTokens.TextSecondary,
            selectedContainerColor = OrderDesignTokens.MintAccent,
            selectedLabelColor = OrderDesignTokens.ForestDeep
        )
    )
}
