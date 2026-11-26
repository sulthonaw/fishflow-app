
package com.fishflow.app.feature.seller.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fishflow.app.core.util.ErrorMessageMapper
import com.fishflow.app.data.model.FishProduct
import com.fishflow.app.data.repository.ProductRepository
import com.fishflow.app.data.repository.ProductRepositoryImpl
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UpdatePriceUiState(
    val productId: String = "",
    val namaIkan: String = "",
    val hargaLama: Double = 0.0,
    val hargaBaru: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
) {
    val hargaBaruValue: Double?
        get() = hargaBaru.toDoubleOrNull()

    val isPriceValid: Boolean
        get() = hargaBaruValue?.let { it > 0 } ?: false

    val isPriceChanged: Boolean
        get() = hargaBaruValue?.let { it != hargaLama } ?: false

    val canSave: Boolean
        get() = isPriceValid && isPriceChanged && !isSaving
}

class UpdatePriceViewModel(
    private val productRepository: ProductRepository = ProductRepositoryImpl.supabase()
) : ViewModel() {

    private val _uiState = MutableStateFlow(UpdatePriceUiState())
    val uiState = _uiState.asStateFlow()

    fun loadProduct(product: FishProduct) {
        _uiState.value = UpdatePriceUiState(
            productId = product.id,
            namaIkan = product.namaIkan,
            hargaLama = product.hargaPerKg,
            hargaBaru = product.hargaPerKg.toLong().toString()
        )
    }

    fun onPriceChange(newPrice: String) {
        if (newPrice.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(
                hargaBaru = newPrice,
                errorMessage = null,
                successMessage = null
            )
        }
    }

    fun savePrice() {
        val currentState = _uiState.value

        if (!currentState.isPriceValid) {
            _uiState.value = currentState.copy(
                errorMessage = "Harga harus lebih dari Rp0."
            )
            return
        }

        if (!currentState.isPriceChanged) {
            _uiState.value = currentState.copy(
                errorMessage = "Harga baru harus berbeda dari harga saat ini."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = currentState.copy(
                isSaving = true,
                errorMessage = null,
                successMessage = null
            )

            try {
                val newPrice = currentState.hargaBaruValue!!
                productRepository.updatePrice(currentState.productId, newPrice)

                _uiState.value = currentState.copy(
                    hargaLama = newPrice,
                    isSaving = false,
                    errorMessage = null,
                    successMessage = "Harga ${currentState.namaIkan} berhasil diperbarui!"
                )
            } catch (e: CancellationException) {
                throw e
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = ErrorMessageMapper.from(
                        exception,
                        fallback = "Gagal memperbarui harga produk."
                    )
                )
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            successMessage = null
        )
    }
}
