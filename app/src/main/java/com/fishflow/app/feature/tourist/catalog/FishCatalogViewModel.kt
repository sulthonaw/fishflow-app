// FishCatalogViewModel.kt
// Developer: Octavia
//
// ViewModel katalog ikan untuk wisatawan.
// - Pilihan wilayah: produk diambil ulang lewat GetProductsByRegionUseCase
// - Kategori dan kata kunci: menyaring produk yang sudah dimuat, tanpa ambil ulang
// - State disimpan di StateFlow supaya filter tidak hilang saat layar diputar
package com.fishflow.app.feature.tourist.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fishflow.app.data.model.FishCategory
import com.fishflow.app.data.model.FishProduct
import com.fishflow.app.data.repository.DestinationRepositoryImpl
import com.fishflow.app.data.repository.ProductRepositoryImpl
import com.fishflow.app.domain.usecase.product.CatalogFilter
import com.fishflow.app.domain.usecase.product.FilterCatalogProductsUseCase
import com.fishflow.app.domain.usecase.product.GetProductsByRegionUseCase
import com.fishflow.app.domain.usecase.tourism.GetDestinationsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FishCatalogViewModel(
    private val getProductsByRegion: GetProductsByRegionUseCase,
    private val getDestinations: GetDestinationsUseCase,
    private val filterProducts: FilterCatalogProductsUseCase = FilterCatalogProductsUseCase()
) : ViewModel() {

    private val _uiState = MutableStateFlow(FishCatalogUiState())
    val uiState: StateFlow<FishCatalogUiState> = _uiState.asStateFlow()

    // Produk tersedia di wilayah terpilih, sebelum disaring kategori dan kata kunci.
    private var loadedProducts: List<FishProduct> = emptyList()
    private var loadJob: Job? = null

    // Dipanggil setiap katalog dibuka supaya stok yang tampil selalu terbaru.
    fun load() {
        loadRegions()
        loadProducts()
    }

    fun onRegionFilter(wilayah: String?) {
        if (_uiState.value.selectedRegion == wilayah) return
        loadedProducts = emptyList()
        _uiState.update { it.copy(selectedRegion = wilayah, products = emptyList()) }
        loadProducts()
    }

    fun onCategoryFilter(kategori: FishCategory?) {
        _uiState.update { it.copy(selectedCategory = kategori).withFilteredProducts() }
    }

    fun onSearchChange(query: String) {
        _uiState.update { it.copy(searchQuery = query).withFilteredProducts() }
    }

    fun onClearFilter() {
        _uiState.update {
            it.copy(selectedCategory = null, searchQuery = "").withFilteredProducts()
        }
        onRegionFilter(null)
    }

    fun onRetry() {
        load()
    }

    private fun loadProducts() {
        // Permintaan lama dibatalkan supaya hasil wilayah sebelumnya tidak menimpa yang baru.
        loadJob?.cancel()
        // Status memuat diset sebelum launch supaya pesan kosong tidak sempat tampil.
        _uiState.update { it.copy(isLoading = loadedProducts.isEmpty(), errorMessage = null) }
        loadJob = viewModelScope.launch {
            getProductsByRegion(_uiState.value.selectedRegion).fold(
                onSuccess = { products ->
                    loadedProducts = products
                    _uiState.update { it.copy(isLoading = false).withFilteredProducts() }
                },
                onFailure = { error ->
                    loadedProducts = emptyList()
                    _uiState.update {
                        it.copy(
                            products = emptyList(),
                            isLoading = false,
                            errorMessage = error.message ?: "Gagal memuat katalog ikan"
                        )
                    }
                }
            )
        }
    }

    // Filter wilayah bersifat opsional, jadi kegagalan di sini tidak menghalangi katalog.
    private fun loadRegions() {
        viewModelScope.launch {
            getDestinations().onSuccess { destinations ->
                _uiState.update { state ->
                    state.copy(regions = destinations.map { it.wilayah }.distinct().sorted())
                }
            }
        }
    }

    private fun FishCatalogUiState.withFilteredProducts(): FishCatalogUiState = copy(
        products = filterProducts(
            loadedProducts,
            CatalogFilter(
                wilayah = selectedRegion,
                kategori = selectedCategory,
                query = searchQuery
            )
        )
    )

    companion object {
        fun factory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                FishCatalogViewModel(
                    getProductsByRegion = GetProductsByRegionUseCase(
                        ProductRepositoryImpl.default()
                    ),
                    getDestinations = GetDestinationsUseCase(
                        DestinationRepositoryImpl.default()
                    )
                )
            }
        }
    }
}
