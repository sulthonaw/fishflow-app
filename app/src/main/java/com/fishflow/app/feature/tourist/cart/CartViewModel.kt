package com.fishflow.app.feature.tourist.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fishflow.app.R
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
            _uiState.update { it.copy(isLoading = true) }
            val user = authRepository.getCurrentUser()
            if (user == null) {
                _uiState.update { it.copy(items = emptyList(), isLoading = false) }
                return@launch
            }
            val remoteCart = orderRepository.getCartItems(user.id).getOrDefault(emptyList())
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
        viewModelScope.launch {
            val user = authRepository.getCurrentUser() ?: return@launch
            val existing = _uiState.value.items.firstOrNull { it.productId == product.id }
            val targetQty = ((existing?.qty ?: 0) + qtyKg)
                .coerceAtLeast(1)
                .coerceAtMost(product.stokTersedia.toInt().coerceAtLeast(1))
            orderRepository.upsertCartItem(user.id, product.id, targetQty.toDouble())
            loadCart()
        }
    }

    fun onUpdateQuantity(productId: String, newQty: Int) {
        if (newQty <= 0) {
            onRemoveItem(productId)
            return
        }
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            _uiState.update { state ->
                state.copy(
                    items = state.items.map { item ->
                        if (item.productId == productId) item.copy(qty = newQty) else item
                    }
                )
            }
            if (user != null) {
                orderRepository.upsertCartItem(user.id, productId, newQty.toDouble())
            }
        }
    }

    fun onRemoveItem(productId: String) {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            _uiState.update { state ->
                state.copy(items = state.items.filterNot { it.productId == productId })
            }
            if (user != null) {
                orderRepository.removeCartItem(user.id, productId)
            }
        }
    }
}
