package com.fishflow.app.feature.admin

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
import com.fishflow.app.domain.usecase.tourism.GetDestinationsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ManageDestinationViewModel.kt
// Developer: Fatih
//
// Koneksi: Pakai DestinationRepository yang sama dengan fitur tourist/destination.
// Bedanya, di sini ada fungsi insert, update, delete (bukan cuma read).
//
// ViewModel kelola destinasi. CRUD lewat DestinationRepository.
data class DestinationFormState(
    val id: String = "",
    val nama: String = "",
    val wilayah: String = "",
    val deskripsi: String = "",
    val fotoUrl: String = "",
    val latitude: String = "",
    val longitude: String = "",
    val isEditing: Boolean = false,
    val errorMessage: String? = null
)

data class ActivityFormState(
    val id: String = "",
    val destinationId: String = "",
    val nama: String = "",
    val kategori: String = "",
    val biaya: String = "",
    val isAvailable: Boolean = true,
    val isEditing: Boolean = false,
    val errorMessage: String? = null
)

data class ManageDestinationUiState(
    val destinations: List<Destination> = emptyList(),
    val activitiesByDestination: Map<String, List<Activity>> = emptyMap(),
    val expandedDestinationIds: Set<String> = emptySet(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val destinationForm: DestinationFormState? = null,
    val activityForm: ActivityFormState? = null,
    val destinationPendingDelete: Destination? = null,
    val activityPendingDelete: Activity? = null
) {
    fun activitiesFor(destinationId: String): List<Activity> =
        activitiesByDestination[destinationId].orEmpty()
}

class ManageDestinationViewModel(
    private val getDestinations: GetDestinationsUseCase,
    private val getActivities: GetActivitiesByDestinationUseCase,
    private val repository: DestinationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManageDestinationUiState())
    val uiState: StateFlow<ManageDestinationUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            getDestinations().fold(
                onSuccess = { destinations ->
                    val activitiesMap = mutableMapOf<String, List<Activity>>()
                    var partialError: String? = null

                    destinations.forEach { destination ->
                        getActivities(destination.id).onSuccess { activities ->
                            activitiesMap[destination.id] = activities
                        }.onFailure { error ->
                            partialError = error.message
                        }
                    }

                    _uiState.update {
                        it.copy(
                            destinations = destinations,
                            activitiesByDestination = activitiesMap,
                            isLoading = false,
                            errorMessage = partialError
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Gagal memuat destinasi"
                        )
                    }
                }
            )
        }
    }

    fun onToggleExpanded(destinationId: String) {
        _uiState.update { state ->
            val expanded = if (destinationId in state.expandedDestinationIds) {
                state.expandedDestinationIds - destinationId
            } else {
                state.expandedDestinationIds + destinationId
            }
            state.copy(expandedDestinationIds = expanded)
        }
    }

    fun onAddDestinationClick() {
        _uiState.update {
            it.copy(destinationForm = DestinationFormState(), successMessage = null)
        }
    }

    fun onEditDestinationClick(destination: Destination) {
        _uiState.update {
            it.copy(
                destinationForm = DestinationFormState(
                    id = destination.id,
                    nama = destination.nama,
                    wilayah = destination.wilayah,
                    deskripsi = destination.deskripsi,
                    fotoUrl = destination.fotoUrl.orEmpty(),
                    latitude = destination.latitude?.toPlainString().orEmpty(),
                    longitude = destination.longitude?.toPlainString().orEmpty(),
                    isEditing = true
                )
            )
        }
    }

    fun onDestinationFormChange(form: DestinationFormState) {
        _uiState.update {
            it.copy(destinationForm = form.copy(errorMessage = null))
        }
    }

    fun onDismissDestinationForm() {
        _uiState.update { it.copy(destinationForm = null) }
    }

    fun onSaveDestination() {
        val form = _uiState.value.destinationForm ?: return
        val nama = form.nama.trim()
        val wilayah = form.wilayah.trim()
        val deskripsi = form.deskripsi.trim()

        val validationError = when {
            nama.isBlank() -> "Nama destinasi wajib diisi"
            wilayah.isBlank() -> "Wilayah wajib diisi"
            deskripsi.isBlank() -> "Deskripsi wajib diisi"
            form.latitude.isNotBlank() && parseDoubleInput(form.latitude) == null ->
                "Latitude harus berupa angka"
            form.longitude.isNotBlank() && parseDoubleInput(form.longitude) == null ->
                "Longitude harus berupa angka"
            else -> null
        }

        if (validationError != null) {
            _uiState.update {
                it.copy(destinationForm = form.copy(errorMessage = validationError))
            }
            return
        }

        val latitude = parseDoubleInput(form.latitude)
        val longitude = parseDoubleInput(form.longitude)
        val destination = Destination(
            id = form.id,
            nama = nama,
            wilayah = wilayah,
            deskripsi = deskripsi,
            fotoUrl = form.fotoUrl.trim().takeIf { it.isNotBlank() },
            latitude = latitude,
            longitude = longitude
        )

        viewModelScope.launch {
            val result = if (form.isEditing) {
                repository.updateDestination(destination)
            } else {
                repository.createDestination(destination)
            }

            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            destinationForm = null,
                            successMessage = if (form.isEditing) {
                                "Destinasi berhasil diperbarui"
                            } else {
                                "Destinasi berhasil ditambahkan"
                            }
                        )
                    }
                    load()
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            destinationForm = form.copy(
                                errorMessage = error.message ?: "Gagal menyimpan destinasi"
                            )
                        )
                    }
                }
            )
        }
    }

    fun onDeleteDestinationClick(destination: Destination) {
        _uiState.update { it.copy(destinationPendingDelete = destination) }
    }

    fun onDismissDeleteDestination() {
        _uiState.update { it.copy(destinationPendingDelete = null) }
    }

    fun onConfirmDeleteDestination() {
        val destination = _uiState.value.destinationPendingDelete ?: return

        viewModelScope.launch {
            repository.deleteDestination(destination.id).fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            destinationPendingDelete = null,
                            successMessage = "Destinasi berhasil dihapus"
                        )
                    }
                    load()
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            destinationPendingDelete = null,
                            errorMessage = error.message ?: "Gagal menghapus destinasi"
                        )
                    }
                }
            )
        }
    }

    fun onAddActivityClick(destinationId: String) {
        _uiState.update {
            it.copy(
                activityForm = ActivityFormState(destinationId = destinationId),
                successMessage = null
            )
        }
    }

    fun onEditActivityClick(activity: Activity) {
        _uiState.update {
            it.copy(
                activityForm = ActivityFormState(
                    id = activity.id,
                    destinationId = activity.destinationId,
                    nama = activity.nama,
                    kategori = activity.kategori,
                    biaya = activity.biaya?.toPlainString().orEmpty(),
                    isAvailable = activity.isAvailable,
                    isEditing = true
                )
            )
        }
    }

    fun onActivityFormChange(form: ActivityFormState) {
        _uiState.update {
            it.copy(activityForm = form.copy(errorMessage = null))
        }
    }

    fun onDismissActivityForm() {
        _uiState.update { it.copy(activityForm = null) }
    }

    fun onSaveActivity() {
        val form = _uiState.value.activityForm ?: return
        val nama = form.nama.trim()
        val kategori = form.kategori.trim()

        val biaya = if (form.isAvailable) parseDoubleInput(form.biaya) else null
        val validationError = when {
            nama.isBlank() -> "Nama aktivitas wajib diisi"
            kategori.isBlank() -> "Kategori wajib diisi"
            form.isAvailable && form.biaya.isBlank() -> "Biaya wajib diisi"
            form.isAvailable && biaya == null -> "Biaya harus berupa angka"
            form.isAvailable && biaya != null && biaya < 0.0 -> "Biaya tidak boleh negatif"
            else -> null
        }

        if (validationError != null) {
            _uiState.update {
                it.copy(activityForm = form.copy(errorMessage = validationError))
            }
            return
        }

        val activity = Activity(
            id = form.id,
            destinationId = form.destinationId,
            nama = nama,
            kategori = kategori,
            biaya = biaya,
            isAvailable = form.isAvailable
        )

        viewModelScope.launch {
            val result = if (form.isEditing) {
                repository.updateActivity(activity)
            } else {
                repository.createActivity(activity)
            }

            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            activityForm = null,
                            successMessage = if (form.isEditing) {
                                "Aktivitas berhasil diperbarui"
                            } else {
                                "Aktivitas berhasil ditambahkan"
                            }
                        )
                    }
                    load()
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            activityForm = form.copy(
                                errorMessage = error.message ?: "Gagal menyimpan aktivitas"
                            )
                        )
                    }
                }
            )
        }
    }

    fun onDeleteActivityClick(activity: Activity) {
        _uiState.update { it.copy(activityPendingDelete = activity) }
    }

    fun onDismissDeleteActivity() {
        _uiState.update { it.copy(activityPendingDelete = null) }
    }

    fun onConfirmDeleteActivity() {
        val activity = _uiState.value.activityPendingDelete ?: return

        viewModelScope.launch {
            repository.deleteActivity(activity.id).fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            activityPendingDelete = null,
                            successMessage = "Aktivitas berhasil dihapus"
                        )
                    }
                    load()
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            activityPendingDelete = null,
                            errorMessage = error.message ?: "Gagal menghapus aktivitas"
                        )
                    }
                }
            )
        }
    }

    fun onMessageShown() {
        _uiState.update { it.copy(successMessage = null, errorMessage = null) }
    }

    private fun parseDoubleInput(raw: String): Double? {
        val cleaned = raw.trim()
            .replace("Rp", "", ignoreCase = true)
            .replace(" ", "")
        if (cleaned.isBlank()) return null

        val normalized = when {
            cleaned.contains(',') && cleaned.contains('.') ->
                cleaned.replace(".", "").replace(",", ".")
            cleaned.lastIndexOf('.') >= 0 && cleaned.substringAfterLast('.').length == 3 ->
                cleaned.replace(".", "")
            else -> cleaned.replace(",", ".")
        }
        return normalized.toDoubleOrNull()
    }

    private fun Double.toPlainString(): String =
        if (this % 1.0 == 0.0) toLong().toString() else toString()

    companion object {
        fun factory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repository = DestinationRepositoryImpl.default()
                ManageDestinationViewModel(
                    getDestinations = GetDestinationsUseCase(repository),
                    getActivities = GetActivitiesByDestinationUseCase(repository),
                    repository = repository
                )
            }
        }
    }
}
