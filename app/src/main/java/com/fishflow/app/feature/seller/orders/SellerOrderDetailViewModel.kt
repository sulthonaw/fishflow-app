package com.fishflow.app.feature.seller.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fishflow.app.core.util.ErrorMessageMapper
import com.fishflow.app.data.model.Order
import com.fishflow.app.data.repository.OrderRepository
import com.fishflow.app.data.repository.OrderRepositoryImpl
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SellerOrderDetailState(
    val order: Order? = null,
    val isLoading: Boolean = true,
    val isActionRunning: Boolean = false,
    val errorMessage: String? = null
)

class SellerOrderDetailViewModel(
    private val orderRepository: OrderRepository = OrderRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SellerOrderDetailState())
    val uiState: StateFlow<SellerOrderDetailState> = _uiState.asStateFlow()

    private var observeJob: Job? = null

    fun observeOrder(orderId: String) {
        if (orderId.isBlank()) return
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            orderRepository.observeOrderStatus(orderId)
                .catch { err ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = ErrorMessageMapper.from(err, "Gagal memuat detail pesanan.")
                        )
                    }
                }
                .collect { order ->
                    _uiState.update {
                        it.copy(order = order, isLoading = false)
                    }
                }
        }
    }

    fun onConfirmOrder(orderId: String) {
        runAction { orderRepository.confirmOrder(orderId) }
    }

    fun onRejectOrder(orderId: String) {
        runAction { orderRepository.rejectOrder(orderId) }
    }

    fun onCompleteOrder(orderId: String, kodePickup: String) {
        runAction { orderRepository.completeOrder(orderId, kodePickup) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun runAction(block: suspend () -> Result<Unit>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isActionRunning = true, errorMessage = null) }
            block()
                .onSuccess {
                    _uiState.update { it.copy(isActionRunning = false) }
                }
                .onFailure { err ->
                    _uiState.update {
                        it.copy(
                            isActionRunning = false,
                            errorMessage = ErrorMessageMapper.from(err, "Gagal memproses pesanan.")
                        )
                    }
                }
        }
    }
}
