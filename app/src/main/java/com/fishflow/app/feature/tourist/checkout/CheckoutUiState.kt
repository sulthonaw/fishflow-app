// CheckoutUiState.kt
// Developer: Sulthon
//
// State halaman checkout.
// Field: orderItems (List<OrderItem>), subtotal (Double),
//        biayaTambahan (Double — es curah, pembersihan, dll),
//        totalHarga (Double), isProcessing (Boolean),
//        isOrderCreated (Boolean), errorMessage, orderId (String? — setelah berhasil)
package com.fishflow.app.feature.tourist.checkout

import com.fishflow.app.R
import com.fishflow.app.data.model.OrderItem

data class CheckoutUiState(
    val buyerName: String = "Bima Arya",
    val buyerPhone: String = "0812-9842-7890",
    val isBuyerVerified: Boolean = true,
    val orderItems: List<OrderItem> = sampleOrderItems(),
    val biayaTambahan: Double = 5_000.0,
    val pickupPoint: String = "Dermaga Sadranan Pos 3",
    val pickupDeadline: String = "17:00 WIB",
    val isProcessing: Boolean = false,
    val isOrderCreated: Boolean = false,
    val errorMessage: String? = null,
    val orderId: String? = null
) {
    val primaryItem: OrderItem?
        get() = orderItems.firstOrNull()

    val subtotal: Double
        get() = orderItems.sumOf { it.subtotal }

    val totalHarga: Double
        get() = subtotal + biayaTambahan

    val isStockSufficient: Boolean
        get() = orderItems.isNotEmpty() && orderItems.all { it.qty <= it.availableStockKg && it.qty >= 1.0 }

    companion object {
        fun sampleOrderItems(): List<OrderItem> = listOf(
            OrderItem(
                id = "oi-1",
                orderId = "ord-draft-1",
                productId = "prod-tuna-01",
                namaIkan = "Tuna Sirip Kuning Super",
                qty = 3.0,
                hargaSatuan = 45_000.0,
                subtotal = 135_000.0,
                vesselName = "KM Penjual Baruna VI",
                grade = "Grade A",
                availableStockKg = 18.0,
                imageRes = R.drawable.img_tuna_super
            )
        )
    }
}
