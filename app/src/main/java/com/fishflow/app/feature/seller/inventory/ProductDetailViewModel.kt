
package com.fishflow.app.feature.seller.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fishflow.app.data.model.FishProduct
import com.fishflow.app.data.repository.ProductRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProductDetailUiState(
    val product: FishProduct? = null,
    val isLoading: Boolean = false,
    val isUpdatingStatus: Boolean = false,
    val error: String? = null
)

class ProductDetailViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    // Mengambil detail produk berdasarkan ID
    fun loadProduct(productId: String) {
        if (productId.isBlank()) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = "ID produk tidak valid."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                val product = repository.getProductById(productId)

                _uiState.value = _uiState.value.copy(
                    product = product,
                    isLoading = false,
                    error = if (product == null) {
                        "Produk tidak ditemukan."
                    } else {
                        null
                    }
                )

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Gagal mengambil detail produk."
                )
            }
        }
    }

    // Mengubah status produk aktif/nonaktif
    fun toggleStatus() {
        val currentProduct = _uiState.value.product ?: return

        if (_uiState.value.isUpdatingStatus) return

        val newStatus = !currentProduct.status

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isUpdatingStatus = true,
                error = null
            )

            try {
                repository.updateStatus(
                    productId = currentProduct.id,
                    isActive = newStatus
                )

                _uiState.value = _uiState.value.copy(
                    product = currentProduct.copy(status = newStatus),
                    isUpdatingStatus = false
                )

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isUpdatingStatus = false,
                    error = e.message ?: "Gagal memperbarui status produk."
                )
            }
        }
    }

    // Menghapus pesan error dari UI
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
