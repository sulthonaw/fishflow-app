package com.fishflow.app.feature.seller.`catch`

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fishflow.app.core.network.SupabaseClientProvider
import com.fishflow.app.core.util.ErrorMessageMapper
import com.fishflow.app.data.model.FishCatch
import com.fishflow.app.data.model.FishCategory
import com.fishflow.app.data.model.FishProduct
import com.fishflow.app.data.model.ProductStatus
import com.fishflow.app.data.repository.AuthRepository
import com.fishflow.app.data.repository.AuthRepositoryImpl
import com.fishflow.app.data.repository.FishCatchRepository
import com.fishflow.app.data.repository.FishCatchRepositoryImpl
import com.fishflow.app.data.repository.ProductRepository
import com.fishflow.app.data.repository.ProductRepositoryImpl
import com.fishflow.app.data.repository.StorageRepository
import com.fishflow.app.data.repository.StorageRepositoryImpl
import com.fishflow.app.supabase.Tables
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
private data class SellerLocationDto(
    val wilayah: String = "",
    @SerialName("nama_kapal") val namaKapal: String = ""
)

class AddCatchViewModel(
    private val authRepository: AuthRepository = AuthRepositoryImpl(),
    private val fishCatchRepository: FishCatchRepository = FishCatchRepositoryImpl(),
    private val productRepository: ProductRepository = ProductRepositoryImpl.supabase(),
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

    fun onHargaChange(value: String) {
        if (value.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(
                hargaPerKg = value,
                hargaError = null,
                errorMessage = null
            )
        }
    }

    fun onKategoriChange(kategori: FishCategory) {
        _uiState.value = _uiState.value.copy(
            kategori = kategori,
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
            currentState.berat.isBlank() -> "Berat tangkapan wajib diisi."
            beratValue == null -> "Berat harus berupa angka yang valid."
            beratValue <= 0 -> "Berat harus lebih dari 0 kg."
            else -> null
        }

        val hargaValue = currentState.hargaPerKg.toDoubleOrNull()
        val hargaError = when {
            currentState.hargaPerKg.isBlank() -> "Harga per kg wajib diisi."
            hargaValue == null || hargaValue <= 0 -> "Harga harus lebih dari Rp0."
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
            hargaError = hargaError,
            tanggalError = tanggalError
        )

        return jenisIkanError == null &&
            beratError == null &&
            hargaError == null &&
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
                val hargaValue = state.hargaPerKg.toDouble()

                val uploadedFotoUrl = if (fotoBytes != null && fotoBytes.isNotEmpty()) {
                    storageRepository.uploadCatchPhoto(
                        sellerId = seller.id,
                        imageBytes = fotoBytes,
                        fileName = fileName
                    ).getOrThrow()
                } else {
                    state.fotoUri?.toString()
                }

                val savedCatch = fishCatchRepository.recordCatch(
                    FishCatch(
                        id = "",
                        sellerId = seller.id,
                        namaIkan = state.jenisIkan.trim(),
                        beratKg = beratValue,
                        tanggalTangkap = state.tanggalTangkap.trim(),
                        fotoUrl = uploadedFotoUrl
                    )
                ).getOrThrow()

                val sellerInfo = runCatching {
                    SupabaseClientProvider.client.postgrest[Tables.SELLER_PROFILES]
                        .select {
                            filter { eq("user_id", seller.id) }
                        }
                        .decodeSingleOrNull<SellerLocationDto>()
                }.getOrNull()

                val wilayah = sellerInfo?.wilayah?.ifBlank { "Tambakrejo" } ?: "Tambakrejo"
                val lokasi = "TPI $wilayah"

                productRepository.createProduct(
                    product = FishProduct(
                        id = "",
                        sellerId = seller.id,
                        namaIkan = state.jenisIkan.trim(),
                        kategori = state.kategori,
                        hargaPerKg = hargaValue,
                        wilayah = wilayah,
                        lokasiPengambilan = lokasi,
                        namaPenjual = seller.nama,
                        namaKapal = sellerInfo?.namaKapal.orEmpty(),
                        status = ProductStatus.AKTIF,
                        stokTersedia = beratValue,
                        stokTeralokasi = 0.0,
                        batasPengambilan = "17:00 WIB",
                        infoPenanganan = "Ikan segar hasil tangkapan hari ini",
                        fotoUrl = uploadedFotoUrl
                    ),
                    catchId = savedCatch.id
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
        _uiState.value = _uiState.value.copy(isSaved = false)
    }

    fun resetForm() {
        _uiState.value = AddCatchUiState()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
