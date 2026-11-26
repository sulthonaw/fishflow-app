package com.fishflow.app.feature.tourist.order_history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fishflow.app.data.model.Order
import com.fishflow.app.data.repository.AuthRepository
import com.fishflow.app.data.repository.AuthRepositoryImpl
import com.fishflow.app.data.repository.OrderRepository
import com.fishflow.app.data.repository.OrderRepositoryImpl
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OrderHistoryUiState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class OrderHistoryViewModel(
    private val authRepository: AuthRepository = AuthRepositoryImpl(),
    private val orderRepository: OrderRepository = OrderRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderHistoryUiState())
    val uiState: StateFlow<OrderHistoryUiState> = _uiState.asStateFlow()

    private var observeJob: Job? = null

    init {
        refresh()
    }

    fun refresh() {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val user = authRepository.getCurrentUser()
            if (user == null) {
                _uiState.update { it.copy(orders = emptyList(), isLoading = false) }
                return@launch
            }
            orderRepository.observeBuyerOrders(user.id)
                .catch { err ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = err.message ?: "Gagal memuat riwayat pesanan."
                        )
                    }
                }
                .collect { orders ->
                    _uiState.update {
                        it.copy(orders = orders, isLoading = false, errorMessage = null)
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
