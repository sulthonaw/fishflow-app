
package com.fishflow.app.feature.seller.`catch`

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fishflow.app.core.util.ErrorMessageMapper
import com.fishflow.app.data.model.FishCatch
import com.fishflow.app.data.repository.AuthRepository
import com.fishflow.app.data.repository.AuthRepositoryImpl
import com.fishflow.app.data.repository.FishCatchRepository
import com.fishflow.app.data.repository.FishCatchRepositoryImpl
import com.fishflow.app.data.repository.StorageRepository
import com.fishflow.app.data.repository.StorageRepositoryImpl
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddCatchViewModel(
    private val authRepository: AuthRepository = AuthRepositoryImpl(),
    private val fishCatchRepository: FishCatchRepository = FishCatchRepositoryImpl(),
    private val storageRepository: StorageRepository = StorageRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddCatchUiState())
    val uiState = _uiState.asStateFlow()
    
    fun onJenisIkanChange(value: String) {
        _uiState.value = _uiState.value.copy(
            jenisIkan = value,
            jenisIkanError = null,
            errorMessage = null
        )
    }

    fun onBeratChange(value: String) {
        _uiState.value = _uiState.value.copy(
            berat = value,
            beratError = null,
            errorMessage = null
        )
    }

    fun onTanggalChange(value: String) {
        _uiState.value = _uiState.value.copy(
            tanggalTangkap = value,
            tanggalError = null,
            errorMessage = null
        )
    }

    fun onFotoSelected(uri: android.net.Uri?) {
        _uiState.value = _uiState.value.copy(
            fotoUri = uri,
            errorMessage = null
        )
    }

    private fun validateForm(): Boolean {
        val currentState = _uiState.value

        val jenisIkanError = if (currentState.jenisIkan.isBlank()) {
            "Jenis ikan wajib diisi."
        } else {
            null
        }

        val beratValue = currentState.berat
            .replace(",", ".")
            .toDoubleOrNull()

        val beratError = when {
            currentState.berat.isBlank() ->
                "Berat tangkapan wajib diisi."

            beratValue == null ->
                "Berat harus berupa angka yang valid."

            beratValue <= 0 ->
                "Berat harus lebih dari 0 kg."

            else -> null
        }

        val tanggalError = if (currentState.tanggalTangkap.isBlank()) {
            "Tanggal tangkap wajib diisi."
        } else {
            null
        }

        _uiState.value = currentState.copy(
            jenisIkanError = jenisIkanError,
            beratError = beratError,
            tanggalError = tanggalError
        )

        return jenisIkanError == null &&
                beratError == null &&
                tanggalError == null
    }

    fun onSaveCatch(
        fotoBytes: ByteArray? = null,
        fileName: String = "catch.jpg"
    ) {
        if (_uiState.value.isLoading) return

        if (!validateForm()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                isSaved = false,
                errorMessage = null
            )

            try {
                val seller = authRepository.getCurrentUser()
                    ?: error("Sesi penjual tidak ditemukan. Silakan login terlebih dahulu.")
                val state = _uiState.value
                val beratValue = state.berat.replace(",", ".").toDouble()

                val uploadedFotoUrl = if (fotoBytes != null && fotoBytes.isNotEmpty()) {
                    storageRepository.uploadCatchPhoto(
                        sellerId = seller.id,
                        imageBytes = fotoBytes,
                        fileName = fileName
                    ).getOrThrow()
                } else {
                    state.fotoUri?.toString()
                }

                fishCatchRepository.recordCatch(
                    FishCatch(
                        id = "",
                        sellerId = seller.id,
                        namaIkan = state.jenisIkan.trim(),
                        beratKg = beratValue,
                        tanggalTangkap = state.tanggalTangkap.trim(),
                        fotoUrl = uploadedFotoUrl
                    )
                ).getOrThrow()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSaved = true,
                    errorMessage = null
                )

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSaved = false,
                    errorMessage = ErrorMessageMapper.from(
                        e,
                        fallback = "Gagal menyimpan data tangkapan."
                    )
                )
            }
        }
    }

    fun resetSavedState() {
        _uiState.value = _uiState.value.copy(
            isSaved = false
        )
    }

    fun resetForm() {
        _uiState.value = AddCatchUiState()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null
        )
    }
}
