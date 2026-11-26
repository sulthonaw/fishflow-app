package com.fishflow.app.feature.tourist.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fishflow.app.core.util.ErrorMessageMapper
import com.fishflow.app.data.repository.DestinationRepositoryImpl
import com.fishflow.app.domain.usecase.tourism.CalculateBudgetEstimateUseCase
import com.fishflow.app.domain.usecase.tourism.GetActivitiesByDestinationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// BudgetPlannerViewModel.kt
// Developer: Fatih
//
// ViewModel kalkulator anggaran wisata.
//
// Yang perlu dilakukan:
// - Ambil daftar aktivitas berdasarkan destinationId
// - Fungsi onActivityToggle(activityId) -> tambah/hapus dari selectedActivityIds
// - Setiap kali selectedActivityIds berubah, panggil CalculateBudgetEstimateUseCase
// - Update totalBiaya dan catatanBelumTersedia di UiState
// - Fungsi onReset() -> kosongkan semua pilihan
class BudgetPlannerViewModel(
    private val destinationId: String,
    destinationName: String,
    private val getActivities: GetActivitiesByDestinationUseCase,
    private val calculateBudgetEstimate: CalculateBudgetEstimateUseCase,
    initialSelectedIds: Set<String> = emptySet()
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        BudgetPlannerUiState(
            destinationId = destinationId,
            destinationName = destinationName.ifBlank { "Destinasi" },
            selectedActivityIds = initialSelectedIds,
            isLoading = true
        )
    )
    val uiState: StateFlow<BudgetPlannerUiState> = _uiState.asStateFlow()

    init {
        loadActivities()
    }

    private fun loadActivities() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            getActivities(destinationId).fold(
                onSuccess = { activities ->
                    val validSelectedIds = _uiState.value.selectedActivityIds
                        .filterTo(mutableSetOf()) { id -> activities.any { it.id == id } }

                    _uiState.update {
                        it.copy(
                            allActivities = activities,
                            selectedActivityIds = validSelectedIds,
                            isLoading = false
                        )
                    }
                    recalculate(validSelectedIds)
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = ErrorMessageMapper.from(error, "Gagal memuat aktivitas destinasi")
                        )
                    }
                }
            )
        }
    }

    fun onActivityToggle(activityId: String) {
        val updatedIds = _uiState.value.selectedActivityIds.toMutableSet().apply {
            if (!add(activityId)) remove(activityId)
        }
        _uiState.update { it.copy(selectedActivityIds = updatedIds) }
        recalculate(updatedIds)
    }

    fun onReset() {
        _uiState.update { it.copy(selectedActivityIds = emptySet()) }
        recalculate(emptySet())
    }

    fun onRetry() {
        loadActivities()
    }

    private fun recalculate(selectedIds: Set<String>) {
        val selectedActivities = _uiState.value.allActivities.filter { it.id in selectedIds }
        val estimate = calculateBudgetEstimate(selectedActivities)
        _uiState.update {
            it.copy(
                totalBiaya = estimate.totalBiaya,
                catatanBelumTersedia = estimate.catatanBelumTersedia
            )
        }
    }

    companion object {
        fun factory(
            destinationId: String,
            destinationName: String,
            initialSelectedIds: Set<String> = emptySet()
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                BudgetPlannerViewModel(
                    destinationId = destinationId,
                    destinationName = destinationName,
                    getActivities = GetActivitiesByDestinationUseCase(
                        DestinationRepositoryImpl.supabase()
                    ),
                    calculateBudgetEstimate = CalculateBudgetEstimateUseCase(),
                    initialSelectedIds = initialSelectedIds
                )
            }
        }
    }
}
