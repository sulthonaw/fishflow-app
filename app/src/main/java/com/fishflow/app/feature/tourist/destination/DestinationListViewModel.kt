package com.fishflow.app.feature.tourist.destination

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fishflow.app.data.model.Destination
import com.fishflow.app.core.util.ErrorMessageMapper
import com.fishflow.app.data.repository.DestinationRepositoryImpl
import com.fishflow.app.domain.usecase.tourism.GetDestinationsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// DestinationListViewModel.kt
// Developer: Fatih
// ViewModel daftar destinasi.
// - Ambil semua destinasi dari DestinationRepository
// - Fungsi onFilterRegion(wilayah) -> filter
// - Fungsi onSearchChange(query) -> cari berdasarkan nama
data class DestinationListUiState(
    val destinations: List<Destination> = emptyList(),
    val selectedWilayah: String? = null,
    val query: String = "",
    val isLoading: Boolean = true,
    val errorMessage: String? = null
) {
    val wilayahOptions: List<String>
        get() = destinations.map { it.wilayah }.distinct().sorted()

    val filteredDestinations: List<Destination>
        get() {
            val keyword = query.trim()
            return destinations.filter { destination ->
                (selectedWilayah == null || destination.wilayah == selectedWilayah) &&
                    (
                        keyword.isBlank() ||
                            destination.nama.contains(keyword, ignoreCase = true) ||
                            destination.wilayah.contains(keyword, ignoreCase = true) ||
                            destination.deskripsi.contains(keyword, ignoreCase = true)
                        )
            }
        }
}

class DestinationListViewModel(
    private val getDestinations: GetDestinationsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DestinationListUiState())
    val uiState: StateFlow<DestinationListUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            getDestinations().fold(
                onSuccess = { destinations ->
                    _uiState.update { state ->
                        val selected = state.selectedWilayah
                            ?.takeIf { wilayah -> destinations.any { it.wilayah == wilayah } }
                        state.copy(
                            destinations = destinations,
                            selectedWilayah = selected,
                            isLoading = false
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = ErrorMessageMapper.from(error, "Gagal memuat destinasi")
                        )
                    }
                }
            )
        }
    }

    fun onFilterRegion(wilayah: String?) {
        _uiState.update { state ->
            state.copy(
                selectedWilayah = if (state.selectedWilayah == wilayah) null else wilayah
            )
        }
    }

    fun onSearchChange(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    fun onRetry() {
        load()
    }

    companion object {
        fun factory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                DestinationListViewModel(
                    getDestinations = GetDestinationsUseCase(
                        DestinationRepositoryImpl.supabase()
                    )
                )
            }
        }
    }
}
