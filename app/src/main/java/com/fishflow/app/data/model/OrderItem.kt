// OrderItem.kt
// Developer: Sulthon
//
// Data class untuk item di dalam pesanan.
// Field: id, orderId, productId, namaIkan, qty (Double, kg), hargaSatuan, subtotal
// Mapping ke tabel "order_items" di Supabase.
// Satu order bisa punya banyak order_items.
package com.fishflow.app.data.model

import androidx.annotation.DrawableRes

data class OrderItem(
    val id: String,
    val orderId: String,
    val productId: String,
    val namaIkan: String,
    val qty: Double,
    val hargaSatuan: Double,
    val subtotal: Double = qty * hargaSatuan,
    val vesselName: String = "",
    val grade: String = "Grade A",
    val availableStockKg: Double = 18.0,
    @param:DrawableRes val imageRes: Int? = null
)
