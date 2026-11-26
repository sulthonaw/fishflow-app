package com.fishflow.app.feature.tourist.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fishflow.app.core.util.ErrorMessageMapper
import com.fishflow.app.data.model.OrderItem
import com.fishflow.app.data.repository.AuthRepository
import com.fishflow.app.data.repository.AuthRepositoryImpl
import com.fishflow.app.data.repository.OrderRepository
import com.fishflow.app.data.repository.OrderRepositoryImpl
import com.fishflow.app.data.repository.ProductRepository
import com.fishflow.app.data.repository.ProductRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CheckoutViewModel(
    private val authRepository: AuthRepository = AuthRepositoryImpl(),
    private val orderRepository: OrderRepository = OrderRepositoryImpl(),
    private val productRepository: ProductRepository = ProductRepositoryImpl.supabase()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    fun setOrderItems(items: List<OrderItem>, pickupPoint: String = "Dermaga TPI") {
        _uiState.update {
            it.copy(
                orderItems = items,
                pickupPoint = pickupPoint,
                isOrderCreated = false,
                errorMessage = null,
                orderId = null
            )
        }
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            if (user != null) {
                _uiState.update { it.copy(buyerName = user.nama) }
            }
        }
    }

    fun onConfirmOrder(itemsOverride: List<OrderItem>? = null) {
        val current = _uiState.value
        val itemsToOrder = itemsOverride?.takeIf { it.isNotEmpty() } ?: current.orderItems
        if (current.isProcessing || itemsToOrder.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, errorMessage = null) }
            val user = authRepository.getCurrentUser()
            if (user == null) {
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        errorMessage = "Silakan login terlebih dahulu untuk membuat pesanan."
                    )
                }
                return@launch
            }

            val firstProductId = itemsToOrder.first().productId
            val product = runCatching { productRepository.getProductById(firstProductId) }.getOrNull()
            val sellerId = product?.sellerId
            if (sellerId.isNullOrBlank()) {
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        errorMessage = "Data penjual untuk produk ini tidak ditemukan."
                    )
                }
                return@launch
            }

            orderRepository.createOrder(
                buyerId = user.id,
                sellerId = sellerId,
                pickupPoint = current.pickupPoint,
                extraFee = current.biayaTambahan,
                items = itemsToOrder
            ).onSuccess { createdOrderId ->
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        isOrderCreated = true,
                        orderId = createdOrderId,
                        errorMessage = null
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        errorMessage = ErrorMessageMapper.from(err, "Gagal membuat pesanan.")
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun resetCreatedState() {
        _uiState.update { it.copy(isOrderCreated = false, errorMessage = null) }
    }
}
