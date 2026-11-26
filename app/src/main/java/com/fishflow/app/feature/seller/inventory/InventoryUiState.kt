
package com.fishflow.app.feature.seller.inventory

import com.fishflow.app.data.model.FishProduct

data class InventoryUiState(
    val products: List<FishProduct> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
) {
    val filteredProducts: List<FishProduct>
        get() = if (searchQuery.isBlank()) {
            products
        } else {
            products.filter { product ->
                product.namaIkan.contains(
                    searchQuery,
                    ignoreCase = true
                )
            }
        }

    val isEmpty: Boolean
        get() = products.isEmpty()

    val isSearchEmpty: Boolean
        get() = searchQuery.isNotBlank() && filteredProducts.isEmpty()
}
