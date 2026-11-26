package com.fishflow.app.feature.seller.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fishflow.app.core.network.SupabaseClientProvider
import com.fishflow.app.data.model.OrderStatus
import com.fishflow.app.data.model.ProductStatus
import com.fishflow.app.data.repository.AuthRepository
import com.fishflow.app.data.repository.AuthRepositoryImpl
import com.fishflow.app.data.repository.ProductRepository
import com.fishflow.app.data.repository.ProductRepositoryImpl
import com.fishflow.app.supabase.Tables
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
private data class SellerOrderSummaryDto(
    val status: String = "",
    @SerialName("total_harga") val totalHarga: Double = 0.0
)

class SellerDashboardViewModel(
    private val authRepository: AuthRepository = AuthRepositoryImpl(),
    private val productRepository: ProductRepository = ProductRepositoryImpl.supabase()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SellerDashboardUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                val user = authRepository.getCurrentUser()
                val sellerId = user?.id.orEmpty()
                val products = if (sellerId.isNotBlank()) {
                    productRepository.getProductsBySeller(sellerId)
                } else {
                    emptyList()
                }
                val orders = if (sellerId.isNotBlank()) {
                    runCatching {
                        SupabaseClientProvider.client.postgrest[Tables.ORDERS]
                            .select {
                                filter { eq("seller_id", sellerId) }
                            }
                            .decodeList<SellerOrderSummaryDto>()
                    }.getOrDefault(emptyList())
                } else {
                    emptyList()
                }

                val stokAktifKg = products
                    .filter { it.status == ProductStatus.AKTIF }
                    .sumOf { it.stokTersedia }
                val pesananBaru = orders.count {
                    it.status == OrderStatus.MENUNGGU_KONFIRMASI.name
                }
                val omzetTotal = orders
                    .filter { it.status == OrderStatus.SELESAI.name }
                    .sumOf { it.totalHarga }

                _uiState.value = _uiState.value.copy(
                    namaUser = user?.nama?.ifBlank { "Penjual FishFlow" } ?: "Penjual FishFlow",
                    stokAktifKg = stokAktifKg,
                    pesananBaru = pesananBaru,
                    omzetTotal = omzetTotal,
                    inventoryPreview = products.take(4),
                    isLoading = false,
                    errorMessage = null
                )
            } catch (e: CancellationException) {
                throw e
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message ?: "Gagal memuat data dashboard."
                )
            }
        }
    }

    fun onRefresh() {
        loadDashboard()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
