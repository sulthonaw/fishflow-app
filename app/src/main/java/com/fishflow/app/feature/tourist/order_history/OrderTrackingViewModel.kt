package com.fishflow.app.feature.tourist.order_history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

data class OrderTrackingUiState(
    val order: Order? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class OrderTrackingViewModel(
    private val orderRepository: OrderRepository = OrderRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderTrackingUiState())
    val uiState: StateFlow<OrderTrackingUiState> = _uiState.asStateFlow()

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
                            errorMessage = err.message ?: "Gagal memuat status pesanan."
                        )
                    }
                }
                .collect { order ->
                    _uiState.update {
                        it.copy(
                            order = order,
                            isLoading = false,
                            errorMessage = if (order == null) "Pesanan tidak ditemukan." else null
                        )
                    }
                }
        }
    }

    fun cancelOrder(orderId: String) {
        viewModelScope.launch {
            orderRepository.cancelOrder(orderId)
                .onFailure { err ->
                    _uiState.update {
                        it.copy(errorMessage = err.message ?: "Gagal membatalkan pesanan.")
                    }
                }
        }
    }
}
