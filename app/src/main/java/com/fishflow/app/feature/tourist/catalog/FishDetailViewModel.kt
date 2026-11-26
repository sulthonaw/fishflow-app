// FishDetailViewModel.kt
// Developer: Octavia
//
// ViewModel detail ikan. Ambil produk by ID (termasuk info penjual dan stok tersedia)
// serta mengatur jumlah yang ingin dipesan. Pembuatan pesanan ditangani Fungsi 2.
package com.fishflow.app.feature.tourist.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fishflow.app.data.model.FishProduct
import com.fishflow.app.data.repository.ProductRepository
import com.fishflow.app.data.repository.ProductRepositoryImpl
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FishDetailUiState(
    val product: FishProduct? = null,
    val quantity: Int = 1,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
) {
    // Keranjang Fungsi 2 memakai kg bulat, jadi stok 12,5 kg bisa dipesan maksimal 12 kg.
    val maxQuantity: Int
        get() = product?.takeIf { it.isAvailable }?.stokTersedia?.toInt() ?: 0

    val canOrder: Boolean
        get() = quantity in 1..maxQuantity
}

class FishDetailViewModel(
    private val productId: String,
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FishDetailUiState())
    val uiState: StateFlow<FishDetailUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    // Dipanggil setiap detail dibuka supaya stok yang tampil selalu terbaru.
    fun load() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = it.product == null, errorMessage = null) }

            repository.getById(productId).fold(
                onSuccess = { product ->
                    _uiState.update { state ->
                        val updated = state.copy(product = product, isLoading = false)
                        updated.copy(
                            quantity = state.quantity.coerceIn(1, updated.maxQuantity.coerceAtLeast(1))
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            product = null,
                            isLoading = false,
                            errorMessage = error.message ?: "Gagal memuat detail produk"
                        )
                    }
                }
            )
        }
    }

    fun onIncreaseQuantity() {
        _uiState.update { state ->
            if (state.quantity < state.maxQuantity) {
                state.copy(quantity = state.quantity + 1)
            } else {
                state
            }
        }
    }

    fun onDecreaseQuantity() {
        _uiState.update { state ->
            if (state.quantity > 1) state.copy(quantity = state.quantity - 1) else state
        }
    }

    fun onRetry() {
        load()
    }

    companion object {
        fun factory(productId: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                FishDetailViewModel(
                    productId = productId,
                    repository = ProductRepositoryImpl.supabase()
                )
            }
        }
    }
}
