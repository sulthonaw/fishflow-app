// Order.kt
// Developer: Sulthon
//
// Data class untuk pesanan.
// Field: id, buyerId, sellerId, totalHarga (Double), status (OrderStatus),
//        kodePickup (String, nullable), createdAt, updatedAt
// Mapping ke tabel "orders" di Supabase.
package com.fishflow.app.data.model

data class Order(
    val id: String,
    val orderCode: String,
    val buyerId: String,
    val buyerName: String,
    val buyerSegment: String,
    val sellerId: String,
    val sellerName: String,
    val vesselName: String,
    val pickupPoint: String,
    val items: List<OrderItem>,
    val extraFee: Double = 5000.0,
    val totalHarga: Double,
    val status: OrderStatus,
    val kodePickup: String? = null,
    val createdAt: String,
    val updatedAt: String = createdAt
)
