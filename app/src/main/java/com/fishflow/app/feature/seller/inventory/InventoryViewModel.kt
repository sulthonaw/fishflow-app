package com.fishflow.app.feature.seller.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fishflow.app.core.util.ErrorMessageMapper
import com.fishflow.app.data.model.ProductStatus
import com.fishflow.app.data.repository.AuthRepository
import com.fishflow.app.data.repository.AuthRepositoryImpl
import com.fishflow.app.data.repository.ProductRepository
import com.fishflow.app.data.repository.ProductRepositoryImpl
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InventoryViewModel(
    private val authRepository: AuthRepository = AuthRepositoryImpl(),
    private val productRepository: ProductRepository = ProductRepositoryImpl.supabase()
) : ViewModel() {

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
                val user = authRepository.getCurrentUser()
                val products = if (user != null) {
                    productRepository.getProductsBySeller(user.id)
                } else {
                    emptyList()
                }
                _uiState.value = _uiState.value.copy(
                    products = products,
                    isLoading = false,
                    errorMessage = null
                )
            } catch (e: CancellationException) {
                throw e
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = ErrorMessageMapper.from(exception, "Gagal memuat inventory.")
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun onRefresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isRefreshing = true,
                errorMessage = null
            )

            try {
                val user = authRepository.getCurrentUser()
                val products = if (user != null) {
                    productRepository.getProductsBySeller(user.id)
                } else {
                    emptyList()
                }
                _uiState.value = _uiState.value.copy(
                    products = products,
                    isRefreshing = false
                )
            } catch (e: CancellationException) {
                throw e
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    isRefreshing = false,
                    errorMessage = ErrorMessageMapper.from(exception, "Gagal memperbarui inventory.")
                )
            }
        }
    }

    fun onToggleStatus(productId: String) {
        val target = _uiState.value.products.firstOrNull { it.id == productId } ?: return
        val newStatus = if (target.status == ProductStatus.AKTIF) {
            ProductStatus.NONAKTIF
        } else {
            ProductStatus.AKTIF
        }

        viewModelScope.launch {
            try {
                productRepository.updateStatus(
                    productId = productId,
                    isActive = (newStatus == ProductStatus.AKTIF)
                )
                val updatedProducts = _uiState.value.products.map { product ->
                    if (product.id == productId) product.copy(status = newStatus) else product
                }
                _uiState.value = _uiState.value.copy(products = updatedProducts)
            } catch (e: CancellationException) {
                throw e
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = ErrorMessageMapper.from(exception, "Gagal mengubah status produk.")
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
