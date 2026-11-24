// FishCatalogUiState.kt
// Developer: Octavia
//
// State untuk katalog ikan.
// - products: hasil akhir setelah semua filter, ini yang ditampilkan di layar
// - regions: pilihan wilayah dari data destinasi Fungsi 3
// - selectedRegion / selectedCategory: null berarti semua
package com.fishflow.app.feature.tourist.catalog

import com.fishflow.app.data.model.FishCategory
import com.fishflow.app.data.model.FishProduct

data class FishCatalogUiState(
    val products: List<FishProduct> = emptyList(),
    val regions: List<String> = emptyList(),
    val categories: List<FishCategory> = FishCategory.entries,
    val selectedRegion: String? = null,
    val selectedCategory: FishCategory? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val errorMessage: String? = null
) {
    val isEmpty: Boolean
        get() = !isLoading && errorMessage == null && products.isEmpty()

    val hasActiveFilter: Boolean
        get() = selectedRegion != null || selectedCategory != null || searchQuery.isNotBlank()
}
