// CartUiState.kt
// Developer: Sulthon
//
// State untuk halaman keranjang belanja.
// Field: items (List<CartItem> — productId, namaIkan, qty, hargaSatuan, subtotal),
//        totalHarga (Double), isStockValid (Boolean — semua item stoknya cukup),
//        stockErrors (Map<productId, String> — pesan error per item jika stok kurang),
//        isLoading, isEmpty
package com.fishflow.app.feature.tourist.cart

import androidx.annotation.DrawableRes
import com.fishflow.app.R

data class CartItem(
    val productId: String,
    val namaIkan: String,
    val vesselName: String,
    val qty: Int,
    val hargaSatuan: Double,
    val availableStockKg: Int,
    val badgeLabel: String = "Segar",
    @param:DrawableRes val imageRes: Int = R.drawable.img_tuna_super
) {
    val subtotal: Double
        get() = qty * hargaSatuan
}

data class CartUiState(
    val items: List<CartItem> = sampleCartItems(),
    val isLoading: Boolean = false
) {
    val isEmpty: Boolean
        get() = items.isEmpty()

    val stockErrors: Map<String, String>
        get() = items.mapNotNull { item ->
            if (item.qty > item.availableStockKg) {
                item.productId to "Stok tidak cukup (tersedia ${item.availableStockKg} kg)"
            } else {
                null
            }
        }.toMap()

    val isStockValid: Boolean
        get() = items.isNotEmpty() && stockErrors.isEmpty()

    val totalHarga: Double
        get() = items.sumOf { it.subtotal }

    companion object {
        fun sampleCartItems(): List<CartItem> = listOf(
            CartItem(
                productId = "prod-tuna-01",
                namaIkan = "Tuna Sirip Kuning Super",
                vesselName = "KM Penjual Baruna VI",
                qty = 3,
                hargaSatuan = 45_000.0,
                availableStockKg = 18,
                badgeLabel = "Segar",
                imageRes = R.drawable.img_tuna_super
            ),
            CartItem(
                productId = "prod-kakap-02",
                namaIkan = "Kakap Merah Segar",
                vesselName = "KM Samudera 02",
                qty = 2,
                hargaSatuan = 65_000.0,
                availableStockKg = 6,
                badgeLabel = "Segar",
                imageRes = R.drawable.img_kakap_merah
            )
        )
    }
}
