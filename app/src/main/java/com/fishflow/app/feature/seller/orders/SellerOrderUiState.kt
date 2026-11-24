// SellerOrderUiState.kt
// Developer: Sulthon
//
// State untuk halaman daftar pesanan (sisi penjual).
// Field: orders (List<Order>), selectedFilter (OrderStatus? — filter by status),
//        isLoading, errorMessage
//
// State detail: order (Order), items (List<OrderItem>), buyerName (String),
//               isConfirming, isRejecting, actionError
package com.fishflow.app.feature.seller.orders

import com.fishflow.app.R
import com.fishflow.app.data.model.Order
import com.fishflow.app.data.model.OrderItem
import com.fishflow.app.data.model.OrderStatus

enum class SellerOrderFilter(val label: String) {
    SEMUA("Semua"),
    MENUNGGU("Menunggu"),
    DIKONFIRMASI("Dikonfirmasi"),
    SELESAI("Selesai")
}

data class SellerOrderUiState(
    val vesselName: String = "KM Bahari 02",
    val harborName: String = "Dermaga Muara Baru",
    val orders: List<Order> = sampleSellerOrders(),
    val selectedFilter: SellerOrderFilter = SellerOrderFilter.SEMUA,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val waitingCount: Int
        get() = orders.count { it.status == OrderStatus.MENUNGGU_KONFIRMASI }

    val readyCount: Int
        get() = orders.count { it.status == OrderStatus.SIAP_DIAMBIL || it.status == OrderStatus.DIKONFIRMASI }

    val completedTodayCount: Int
        get() = orders.count { it.status == OrderStatus.SELESAI }

    val completedTodayKg: Double
        get() = orders.filter { it.status == OrderStatus.SELESAI }.sumOf { order -> order.items.sumOf { it.qty } }

    val completedTodayRevenue: Double
        get() = orders.filter { it.status == OrderStatus.SELESAI }.sumOf { it.totalHarga }

    companion object {
        fun sampleSellerOrders(): List<Order> = listOf(
            Order(
                id = "ord-7801",
                orderCode = "#ORD-7801",
                buyerId = "buyer-bima",
                buyerName = "Bima Arya",
                buyerSegment = "WISATAWAN DOMESTIK",
                sellerId = "seller-joko",
                sellerName = "Pak Joko Widodo",
                vesselName = "KM Bahari 02",
                pickupPoint = "Dermaga Muara Baru",
                items = listOf(
                    OrderItem(
                        id = "oi-7801",
                        orderId = "ord-7801",
                        productId = "prod-tuna-01",
                        namaIkan = "Tuna Sirip Kuning Super",
                        qty = 3.0,
                        hargaSatuan = 45_000.0,
                        subtotal = 135_000.0,
                        vesselName = "KM Bahari 02",
                        grade = "Grade A",
                        availableStockKg = 18.0,
                        imageRes = R.drawable.img_tuna_super
                    )
                ),
                extraFee = 5_000.0,
                totalHarga = 140_000.0,
                status = OrderStatus.MENUNGGU_KONFIRMASI,
                kodePickup = null,
                createdAt = "07:12 WIB"
            ),
            Order(
                id = "ord-7802",
                orderCode = "#ORD-7802",
                buyerId = "buyer-andi",
                buyerName = "Andi Pratama",
                buyerSegment = "WISATAWAN DOMESTIK",
                sellerId = "seller-joko",
                sellerName = "Pak Joko Widodo",
                vesselName = "KM Bahari 02",
                pickupPoint = "Dermaga Muara Baru",
                items = listOf(
                    OrderItem(
                        id = "oi-7802",
                        orderId = "ord-7802",
                        productId = "prod-kakap-02",
                        namaIkan = "Kakap Merah Segar",
                        qty = 2.0,
                        hargaSatuan = 65_000.0,
                        subtotal = 130_000.0,
                        vesselName = "KM Bahari 02",
                        grade = "Grade A",
                        availableStockKg = 10.0,
                        imageRes = R.drawable.img_kakap_merah
                    )
                ),
                extraFee = 5_000.0,
                totalHarga = 135_000.0,
                status = OrderStatus.MENUNGGU_KONFIRMASI,
                kodePickup = null,
                createdAt = "07:20 WIB"
            ),
            Order(
                id = "ord-9021",
                orderCode = "#ORD-9021",
                buyerId = "buyer-siti",
                buyerName = "Siti Rahma",
                buyerSegment = "WISATAWAN DOMESTIK",
                sellerId = "seller-joko",
                sellerName = "Pak Joko Widodo",
                vesselName = "KM Bahari 02",
                pickupPoint = "Pos 3 TPI Barat",
                items = listOf(
                    OrderItem(
                        id = "oi-9021",
                        orderId = "ord-9021",
                        productId = "prod-kakap-02",
                        namaIkan = "Kakap Merah Segar",
                        qty = 1.5,
                        hargaSatuan = 65_000.0,
                        subtotal = 97_500.0,
                        vesselName = "KM Bahari 02",
                        grade = "Grade A",
                        availableStockKg = 10.0,
                        imageRes = R.drawable.img_kakap_merah
                    )
                ),
                extraFee = 0.0,
                totalHarga = 97_500.0,
                status = OrderStatus.SIAP_DIAMBIL,
                kodePickup = "FF-9021",
                createdAt = "06:45 WIB"
            ),
            Order(
                id = "ord-7750",
                orderCode = "#ORD-7750",
                buyerId = "buyer-dewi",
                buyerName = "Dewi Lestari",
                buyerSegment = "WISATAWAN DOMESTIK",
                sellerId = "seller-joko",
                sellerName = "Pak Joko Widodo",
                vesselName = "KM Bahari 02",
                pickupPoint = "Pos 3 TPI Barat",
                items = listOf(
                    OrderItem(
                        id = "oi-7750",
                        orderId = "ord-7750",
                        productId = "prod-tuna-01",
                        namaIkan = "Tuna Sirip Kuning Super",
                        qty = 16.0,
                        hargaSatuan = 45_000.0,
                        subtotal = 720_000.0,
                        vesselName = "KM Bahari 02",
                        grade = "Grade A",
                        availableStockKg = 18.0,
                        imageRes = R.drawable.img_tuna_super
                    )
                ),
                extraFee = 22_000.0,
                totalHarga = 742_000.0,
                status = OrderStatus.SELESAI,
                kodePickup = "FF-7750",
                createdAt = "06:00 WIB"
            )
        )
    }
}

data class SellerOrderDetailUiState(
    val order: Order,
    val isConfirming: Boolean = false,
    val isRejecting: Boolean = false,
    val actionError: String? = null
)
