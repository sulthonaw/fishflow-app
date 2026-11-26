package com.fishflow.app.feature.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fishflow.app.core.network.SupabaseClientProvider
import com.fishflow.app.data.remote.ProfileDto
import com.fishflow.app.data.repository.DestinationRepositoryImpl
import com.fishflow.app.domain.usecase.tourism.GetActivitiesByDestinationUseCase
import com.fishflow.app.domain.usecase.tourism.GetDestinationsUseCase
import com.fishflow.app.supabase.Tables
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// AdminDashboardViewModel.kt
// Developer: Fatih
//
// Koneksi: ViewModel ini pakai DestinationRepository yang sama
// dengan fitur destinasi wisata milik Fatih.
//
// ViewModel admin dashboard. Ambil statistik: jumlah destinasi, jumlah user, dll.
data class AdminDashboardUiState(
    val totalDestinations: Int = 0,
    val totalActivities: Int = 0,
    val totalUsers: Int = 0,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class AdminDashboardViewModel(
    private val getDestinations: GetDestinationsUseCase,
    private val getActivities: GetActivitiesByDestinationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminDashboardUiState())
    val uiState: StateFlow<AdminDashboardUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            getDestinations().fold(
                onSuccess = { destinations ->
                    var activityCount = 0
                    var partialError: String? = null

                    destinations.forEach { destination ->
                        getActivities(destination.id).onSuccess {
                            activityCount += it.size
                        }.onFailure { error ->
                            partialError = error.message
                        }
                    }

                    val userCount = runCatching {
                        SupabaseClientProvider.client.postgrest[Tables.PROFILES]
                            .select()
                            .decodeList<ProfileDto>()
                            .size
                    }.getOrElse { AdminUserDirectory.count() }

                    _uiState.update {
                        it.copy(
                            totalDestinations = destinations.size,
                            totalActivities = activityCount,
                            totalUsers = userCount,
                            isLoading = false,
                            errorMessage = partialError
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Gagal memuat statistik"
                        )
                    }
                }
            )
        }
    }

    companion object {
        fun factory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repository = DestinationRepositoryImpl.supabase()
                AdminDashboardViewModel(
                    getDestinations = GetDestinationsUseCase(repository),
                    getActivities = GetActivitiesByDestinationUseCase(repository)
                )
            }
        }
    }
}
