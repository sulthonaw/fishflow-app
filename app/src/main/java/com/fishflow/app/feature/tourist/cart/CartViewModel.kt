package com.fishflow.app.feature.tourist.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fishflow.app.R
import com.fishflow.app.core.util.ErrorMessageMapper
import com.fishflow.app.data.model.FishProduct
import com.fishflow.app.data.repository.AuthRepository
import com.fishflow.app.data.repository.AuthRepositoryImpl
import com.fishflow.app.data.repository.OrderRepository
import com.fishflow.app.data.repository.OrderRepositoryImpl
import com.fishflow.app.data.repository.ProductRepository
import com.fishflow.app.data.repository.ProductRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CartViewModel(
    private val authRepository: AuthRepository = AuthRepositoryImpl(),
    private val orderRepository: OrderRepository = OrderRepositoryImpl(),
    private val productRepository: ProductRepository = ProductRepositoryImpl.supabase()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState(items = emptyList(), isLoading = true))
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        loadCart()
    }

    fun loadCart() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = it.items.isEmpty(), errorMessage = null) }
            val user = authRepository.getCurrentUser()
            if (user == null) {
                _uiState.update { it.copy(items = emptyList(), isLoading = false) }
                return@launch
            }
            val remoteResult = orderRepository.getCartItems(user.id)
            if (remoteResult.isFailure) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = ErrorMessageMapper.from(
                            remoteResult.exceptionOrNull(),
                            "Gagal memuat keranjang."
                        )
                    )
                }
                return@launch
            }
            val remoteCart = remoteResult.getOrDefault(emptyList())
            val products = runCatching { productRepository.getActiveProducts() }
                .getOrDefault(emptyList())
                .associateBy { it.id }

            val cartItems = remoteCart.mapNotNull { dto ->
                val product = products[dto.productId] ?: return@mapNotNull null
                CartItem(
                    productId = product.id,
                    namaIkan = product.namaIkan,
                    vesselName = product.namaKapal?.ifBlank { product.namaPenjual } ?: product.namaPenjual,
                    qty = dto.qtyKg.toInt().coerceAtLeast(1),
                    hargaSatuan = product.hargaPerKg,
                    availableStockKg = product.stokTersedia.toInt().coerceAtLeast(0),
                    badgeLabel = product.kategori.label,
                    imageRes = product.imageRes ?: R.drawable.img_tuna_super
                )
            }
            _uiState.update { it.copy(items = cartItems, isLoading = false) }
        }
    }

    fun onAddItem(product: FishProduct, qtyKg: Int) {
        val maxStock = product.stokTersedia.toInt().coerceAtLeast(1)
        val existing = _uiState.value.items.firstOrNull { it.productId == product.id }
        val targetQty = ((existing?.qty ?: 0) + qtyKg)
            .coerceAtLeast(1)
            .coerceAtMost(maxStock)

        val optimisticItem = CartItem(
            productId = product.id,
            namaIkan = product.namaIkan,
            vesselName = product.namaKapal?.ifBlank { product.namaPenjual } ?: product.namaPenjual,
            qty = targetQty,
            hargaSatuan = product.hargaPerKg,
            availableStockKg = maxStock,
            badgeLabel = product.kategori.label,
            imageRes = product.imageRes ?: R.drawable.img_tuna_super
        )

        // Update state secara langsung agar langsung tampil di CartScreen tanpa jeda kosong.
        _uiState.update { state ->
            val remaining = state.items.filterNot { it.productId == product.id }
            state.copy(
                items = listOf(optimisticItem) + remaining,
                isLoading = false,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            val user = authRepository.getCurrentUser() ?: return@launch
            orderRepository.upsertCartItem(user.id, product.id, targetQty.toDouble())
                .onFailure { err ->
                    _uiState.update {
                        it.copy(
                            errorMessage = ErrorMessageMapper.from(
                                err,
                                "Gagal menyimpan item ke keranjang."
                            )
                        )
                    }
                }
        }
    }

    fun onUpdateQuantity(productId: String, newQty: Int) {
        if (newQty <= 0) {
            onRemoveItem(productId)
            return
        }
        _uiState.update { state ->
            state.copy(
                items = state.items.map { item ->
                    if (item.productId == productId) item.copy(qty = newQty) else item
                }
            )
        }
        viewModelScope.launch {
            val user = authRepository.getCurrentUser() ?: return@launch
            orderRepository.upsertCartItem(user.id, productId, newQty.toDouble())
                .onFailure { err ->
                    _uiState.update {
                        it.copy(
                            errorMessage = ErrorMessageMapper.from(
                                err,
                                "Gagal memperbarui jumlah pesanan."
                            )
                        )
                    }
                }
        }
    }

    fun onRemoveItem(productId: String) {
        _uiState.update { state ->
            state.copy(items = state.items.filterNot { it.productId == productId })
        }
        viewModelScope.launch {
            val user = authRepository.getCurrentUser() ?: return@launch
            orderRepository.removeCartItem(user.id, productId)
                .onFailure { err ->
                    _uiState.update {
                        it.copy(
                            errorMessage = ErrorMessageMapper.from(
                                err,
                                "Gagal menghapus item dari keranjang."
                            )
                        )
                    }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
