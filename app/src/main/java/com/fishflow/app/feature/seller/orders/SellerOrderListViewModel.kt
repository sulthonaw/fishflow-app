package com.fishflow.app.feature.seller.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fishflow.app.core.util.ErrorMessageMapper
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

class SellerOrderListViewModel(
    private val authRepository: AuthRepository = AuthRepositoryImpl(),
    private val orderRepository: OrderRepository = OrderRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SellerOrderUiState(orders = emptyList(), isLoading = true))
    val uiState: StateFlow<SellerOrderUiState> = _uiState.asStateFlow()

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
            orderRepository.observeSellerOrders(user.id)
                .catch { err ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = ErrorMessageMapper.from(err, "Gagal memuat pesanan penjual.")
                        )
                    }
                }
                .collect { orders ->
                    val first = orders.firstOrNull()
                    _uiState.update {
                        it.copy(
                            vesselName = first?.vesselName?.ifBlank { it.vesselName } ?: it.vesselName,
                            harborName = first?.pickupPoint?.ifBlank { it.harborName } ?: it.harborName,
                            orders = orders,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
        }
    }

    fun onFilterChange(filter: SellerOrderFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    fun confirmOrder(orderId: String) {
        viewModelScope.launch {
            orderRepository.confirmOrder(orderId)
                .onFailure { err ->
                    _uiState.update {
                        it.copy(errorMessage = ErrorMessageMapper.from(err, "Gagal mengonfirmasi pesanan."))
                    }
                }
        }
    }

    fun rejectOrder(orderId: String) {
        viewModelScope.launch {
            orderRepository.rejectOrder(orderId)
                .onFailure { err ->
                    _uiState.update {
                        it.copy(errorMessage = ErrorMessageMapper.from(err, "Gagal menolak pesanan."))
                    }
                }
        }
    }

    fun completeOrder(orderId: String, kodePickup: String) {
        viewModelScope.launch {
            orderRepository.completeOrder(orderId, kodePickup)
                .onFailure { err ->
                    _uiState.update {
                        it.copy(
                            errorMessage = ErrorMessageMapper.from(
                                err,
                                "Kode pickup tidak valid atau gagal menyelesaikan pesanan."
                            )
                        )
                    }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
