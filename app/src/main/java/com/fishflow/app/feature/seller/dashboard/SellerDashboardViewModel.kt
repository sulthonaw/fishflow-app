package com.fishflow.app.feature.seller.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SellerDashboardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        SellerDashboardUiState()
    )

    val uiState = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                delay(800)

                _uiState.value = _uiState.value.copy(
                    namaUser = "Penjual FishFlow",
                    stokAktifKg = 42.5,
                    pesananBaru = 2,
                    omzetTotal = 680000.0,
                    inventoryPreview = emptyList(),
                    isLoading = false,
                    errorMessage = null
                )

            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message
                        ?: "Gagal memuat data dashboard."
                )
            }
        }
    }

    fun onRefresh() {
        loadDashboard()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null
        )
    }
}
