package com.fishflow.app.feature.tourist.destination

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fishflow.app.data.model.Activity
import com.fishflow.app.data.model.Destination
import com.fishflow.app.data.repository.DestinationRepository
import com.fishflow.app.data.repository.DestinationRepositoryImpl
import com.fishflow.app.domain.usecase.tourism.GetActivitiesByDestinationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// DestinationDetailViewModel.kt
// Developer: Fatih
// ViewModel detail destinasi.
// - Ambil data destinasi by ID
// - Ambil daftar aktivitas + biaya dari GetActivitiesByDestinationUseCase
data class DestinationDetailUiState(
    val destination: Destination? = null,
    val activities: List<Activity> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class DestinationDetailViewModel(
    private val destinationId: String,
    private val repository: DestinationRepository,
    private val getActivities: GetActivitiesByDestinationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DestinationDetailUiState())
    val uiState: StateFlow<DestinationDetailUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            repository.getById(destinationId).fold(
                onSuccess = { destination ->
                    getActivities(destinationId).fold(
                        onSuccess = { activities ->
                            _uiState.update {
                                it.copy(
                                    destination = destination,
                                    activities = activities,
                                    isLoading = false
                                )
                            }
                        },
                        onFailure = { error ->
                            _uiState.update {
                                it.copy(
                                    destination = destination,
                                    isLoading = false,
                                    errorMessage = error.message
                                        ?: "Gagal memuat aktivitas destinasi"
                                )
                            }
                        }
                    )
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Destinasi tidak ditemukan"
                        )
                    }
                }
            )
        }
    }

    fun onRetry() {
        load()
    }

    companion object {
        fun factory(destinationId: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repository = DestinationRepositoryImpl.default()
                DestinationDetailViewModel(
                    destinationId = destinationId,
                    repository = repository,
                    getActivities = GetActivitiesByDestinationUseCase(repository)
                )
            }
        }
    }
}
