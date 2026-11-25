// TouristHomeViewModel.kt
// Developer: Octavia
//
// ViewModel beranda wisatawan.
// - onRefresh(): ambil tangkapan hari ini dan destinasi unggulan, masing-masing 4 data pertama
// - onCategoryClick(category): ganti section yang tampil (ikan atau destinasi)
// - onSearchChange(query): simpan kata kunci yang nanti diteruskan ke katalog
//
// Perpindahan layar (produk, destinasi, pencarian) lewat callback di TouristHomeScreen,
// sama seperti layar lain.
package com.fishflow.app.feature.tourist.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fishflow.app.data.repository.DestinationRepositoryImpl
import com.fishflow.app.data.repository.ProductRepositoryImpl
import com.fishflow.app.domain.usecase.product.GetProductsByRegionUseCase
import com.fishflow.app.domain.usecase.tourism.GetDestinationsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TouristHomeViewModel(
    private val getProducts: GetProductsByRegionUseCase,
    private val getDestinations: GetDestinationsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TouristHomeUiState())
    val uiState: StateFlow<TouristHomeUiState> = _uiState.asStateFlow()

    private var catchJob: Job? = null
    private var destinationJob: Job? = null

    // Dipanggil setiap beranda dibuka supaya stok yang tampil selalu terbaru.
    fun onRefresh() {
        loadTodayCatches()
        loadDestinations()
    }

    fun onCategoryClick(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun onSearchChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    private fun loadTodayCatches() {
        catchJob?.cancel()
        _uiState.update { it.copy(isLoading = it.todayCatches.isEmpty(), errorMessage = null) }
        catchJob = viewModelScope.launch {
            getProducts().fold(
                onSuccess = { products ->
                    _uiState.update {
                        it.copy(
                            todayCatches = products.take(SECTION_LIMIT),
                            isLoading = false
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            todayCatches = emptyList(),
                            isLoading = false,
                            errorMessage = error.message ?: "Gagal memuat tangkapan hari ini"
                        )
                    }
                }
            )
        }
    }

    private fun loadDestinations() {
        destinationJob?.cancel()
        _uiState.update {
            it.copy(
                isDestinationLoading = it.featuredDestinations.isEmpty(),
                destinationErrorMessage = null
            )
        }
        destinationJob = viewModelScope.launch {
            getDestinations().fold(
                onSuccess = { destinations ->
                    _uiState.update {
                        it.copy(
                            featuredDestinations = destinations.take(SECTION_LIMIT),
                            isDestinationLoading = false
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            featuredDestinations = emptyList(),
                            isDestinationLoading = false,
                            destinationErrorMessage = error.message ?: "Gagal memuat destinasi"
                        )
                    }
                }
            )
        }
    }

    companion object {
        private const val SECTION_LIMIT = 4

        fun factory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                TouristHomeViewModel(
                    getProducts = GetProductsByRegionUseCase(ProductRepositoryImpl.default()),
                    getDestinations = GetDestinationsUseCase(DestinationRepositoryImpl.default())
                )
            }
        }
    }
}
