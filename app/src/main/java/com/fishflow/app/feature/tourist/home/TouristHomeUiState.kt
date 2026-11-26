// TouristHomeUiState.kt
// Developer: Octavia
//
// State beranda wisatawan.
// Field: featuredDestinations (List<Destination> — destinasi unggulan),
//        todayCatches (List<FishProduct> — tangkapan hari ini),
//        categories (List<String> — "Ikan Segar", "Wisata Bahari") dan selectedCategory,
//        searchQuery, isLoading, errorMessage
//
// Destinasi punya status memuat dan error sendiri supaya kegagalannya tidak menutupi ikan.
// "Sewa Kapal" tidak ditampilkan karena layarnya belum ada.
package com.fishflow.app.feature.tourist.home

import com.fishflow.app.data.model.Destination
import com.fishflow.app.data.model.FishProduct

data class TouristHomeUiState(
    val featuredDestinations: List<Destination> = emptyList(),
    val todayCatches: List<FishProduct> = emptyList(),
    val categories: List<String> = listOf(CATEGORY_FISH, CATEGORY_TOURISM),
    val selectedCategory: String = CATEGORY_FISH,
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isDestinationLoading: Boolean = true,
    val destinationErrorMessage: String? = null
) {
    val isEmpty: Boolean
        get() = !isLoading && errorMessage == null && todayCatches.isEmpty()

    val isDestinationEmpty: Boolean
        get() = !isDestinationLoading && destinationErrorMessage == null &&
            featuredDestinations.isEmpty()

    companion object {
        const val CATEGORY_FISH = "Ikan Segar"
        const val CATEGORY_TOURISM = "Wisata Bahari"
    }
}
