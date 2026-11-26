
package com.fishflow.app.feature.seller.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fishflow.app.data.model.FishProduct
import com.fishflow.app.data.model.ProductStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class InventoryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(InventoryUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadInventory()
    }

    fun loadInventory() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                delay(500)

                val mockProducts = listOf(
                    FishProduct(
                        id = "fish-001",
                        sellerId = "seller-001",
                        namaIkan = "Ikan Tuna",
                        hargaPerKg = 45000.0,
                        stokTersedia = 25.0,
                        stokTeralokasi = 5.0,
                        status = ProductStatus.AKTIF,
                        wilayah = "Pantai Tambakrejo"
                    ),
                    FishProduct(
                        id = "fish-002",
                        sellerId = "seller-001",
                        namaIkan = "Ikan Lele",
                        hargaPerKg = 28000.0,
                        stokTersedia = 2.0,
                        stokTeralokasi = 0.0,
                        status = ProductStatus.AKTIF,
                        wilayah = "Pantai Tambakrejo"
                    ),
                    FishProduct(
                        id = "fish-003",
                        sellerId = "seller-001",
                        namaIkan = "Ikan Kakap",
                        hargaPerKg = 55000.0,
                        stokTersedia = 8.0,
                        stokTeralokasi = 2.0,
                        status = ProductStatus.AKTIF,
                        wilayah = "Pantai Tambakrejo"
                    ),
                    FishProduct(
                        id = "fish-004",
                        sellerId = "seller-001",
                        namaIkan = "Ikan Gurame",
                        hargaPerKg = 40000.0,
                        stokTersedia = 15.0,
                        stokTeralokasi = 3.0,
                        status = ProductStatus.NONAKTIF,
                        wilayah = "Pantai Tambakrejo"
                    )
                )

                _uiState.value = _uiState.value.copy(
                    products = mockProducts,
                    isLoading = false,
                    errorMessage = null
                )
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message
                        ?: "Gagal memuat inventory."
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query
        )
    }

    fun onRefresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isRefreshing = true,
                errorMessage = null
            )

            try {
                delay(500)
                _uiState.value = _uiState.value.copy(
                    isRefreshing = false
                )
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    isRefreshing = false,
                    errorMessage = exception.message
                        ?: "Gagal memperbarui inventory."
                )
            }
        }
    }

    fun onToggleStatus(productId: String) {
        val updatedProducts = _uiState.value.products.map { product ->
            if (product.id == productId) {
                val newStatus = if (product.status == ProductStatus.AKTIF) {
                    ProductStatus.NONAKTIF
                } else {
                    ProductStatus.AKTIF
                }
                product.copy(status = newStatus)
            } else {
                product
            }
        }

        _uiState.value = _uiState.value.copy(
            products = updatedProducts
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null
        )
    }
}
