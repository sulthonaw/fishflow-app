
package com.fishflow.app.feature.seller.`catch`

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddCatchViewModel : ViewModel() {

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

    fun onSaveCatch() {
        if (_uiState.value.isLoading) return

        if (!validateForm()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                isSaved = false,
                errorMessage = null
            )

            try {
                delay(1000)

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
                    errorMessage = e.message
                        ?: "Gagal menyimpan data tangkapan."
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
