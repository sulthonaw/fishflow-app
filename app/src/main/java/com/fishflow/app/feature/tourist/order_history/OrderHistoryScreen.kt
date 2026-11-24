// OrderHistoryScreen.kt
// Developer: Sulthon
//
// Riwayat pesanan wisatawan.
//
// UI:
// - LazyColumn daftar pesanan: tanggal, nama penjual, total harga, status
// - Klik → ke OrderTrackingScreen
// - Filter/tab: Aktif (belum selesai) dan Riwayat (sudah selesai/ditolak)
package com.fishflow.app.feature.tourist.order_history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fishflow.app.R
import com.fishflow.app.core.ui.components.OrderDesignTokens
import com.fishflow.app.data.model.Order
import com.fishflow.app.data.model.OrderItem
import com.fishflow.app.data.model.OrderStatus
import com.fishflow.app.feature.tourist.order_history.components.OrderHistoryCard

enum class OrderHistoryTab(val label: String) {
    AKTIF("Aktif"),
    RIWAYAT("Riwayat")
}

@Composable
fun OrderHistoryScreen(
    modifier: Modifier = Modifier,
    orders: List<Order> = sampleTouristOrders(),
    onOrderClick: (Order) -> Unit = {},
    onNavItemClick: (String) -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(OrderHistoryTab.AKTIF) }

    val activeStatuses = setOf(
        OrderStatus.MENUNGGU_KONFIRMASI,
        OrderStatus.DIKONFIRMASI,
        OrderStatus.SIAP_DIAMBIL
    )

    val filteredOrders = remember(orders, selectedTab) {
        when (selectedTab) {
            OrderHistoryTab.AKTIF -> orders.filter { it.status in activeStatuses }
            OrderHistoryTab.RIWAYAT -> orders.filter { it.status !in activeStatuses }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = OrderDesignTokens.CanvasBg,
        topBar = {
            TrackingBrandHeader(title = "Riwayat Pesanan")
        },
        bottomBar = {
            TouristOrderBottomBar(
                activeTab = "Pesanan",
                onTabClick = onNavItemClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            OrderHistoryCoastalHeader(
                selectedTab = selectedTab,
                activeCount = orders.count { it.status in activeStatuses },
                historyCount = orders.count { it.status !in activeStatuses },
                onSelectTab = { selectedTab = it }
            )

            if (filteredOrders.isEmpty()) {
                OrderHistoryEmptyState(tab = selectedTab)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredOrders, key = { it.id }) { order ->
                        OrderHistoryCard(
                            order = order,
                            onClick = { onOrderClick(order) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderHistoryCoastalHeader(
    selectedTab: OrderHistoryTab,
    activeCount: Int,
    historyCount: Int,
    onSelectTab: (OrderHistoryTab) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(OrderDesignTokens.MaritimeDark)
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "PEMANTAUAN TANGKAPAN",
                color = OrderDesignTokens.MintAccent,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "Daftar Pesanan Anda",
                color = Color.White,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(OrderDesignTokens.ForestDeep.copy(alpha = 0.5f))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            OrderHistoryTab.entries.forEach { tab ->
                val isSelected = tab == selectedTab
                val count = if (tab == OrderHistoryTab.AKTIF) activeCount else historyCount
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) OrderDesignTokens.CoastalTeal else Color.Transparent)
                        .clickable { onSelectTab(tab) }
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${tab.label} ($count)",
                        color = if (isSelected) Color.White else OrderDesignTokens.SageLight,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderHistoryEmptyState(tab: OrderHistoryTab) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(OrderDesignTokens.MintAccent.copy(alpha = 0.35f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.ReceiptLong,
                contentDescription = null,
                tint = OrderDesignTokens.CoastalTeal,
                modifier = Modifier.size(30.dp)
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = if (tab == OrderHistoryTab.AKTIF) {
                "Belum ada pesanan aktif"
            } else {
                "Belum ada riwayat pesanan"
            },
            color = OrderDesignTokens.ForestDeep,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Pesanan ikan Anda akan muncul di sini beserta kode pengambilan dermaga.",
            color = OrderDesignTokens.TextSecondary,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )
    }
}

fun sampleTouristOrders(): List<Order> = listOf(
    sampleTrackingOrder(),
    Order(
        id = "ord-ff-095",
        orderCode = "#FF-20261013-095",
        buyerId = "buyer-bima",
        buyerName = "Bima Arya",
        buyerSegment = "WISATAWAN DOMESTIK",
        sellerId = "seller-baruna",
        sellerName = "KM Penjual Baruna VI",
        vesselName = "KM Baruna VI",
        pickupPoint = "Dermaga Sadranan Pos 3",
        items = listOf(
            OrderItem(
                id = "oi-095",
                orderId = "ord-ff-095",
                productId = "prod-kakap-02",
                namaIkan = "Kakap Merah Segar",
                qty = 2.0,
                hargaSatuan = 65_000.0,
                subtotal = 130_000.0,
                vesselName = "KM Baruna VI",
                grade = "Grade A",
                availableStockKg = 10.0,
                imageRes = R.drawable.img_kakap_merah
            )
        ),
        extraFee = 5_000.0,
        totalHarga = 135_000.0,
        status = OrderStatus.MENUNGGU_KONFIRMASI,
        kodePickup = null,
        createdAt = "13 Okt 2026 • 07:45 WIB"
    ),
    Order(
        id = "ord-ff-081",
        orderCode = "#FF-20261011-081",
        buyerId = "buyer-bima",
        buyerName = "Bima Arya",
        buyerSegment = "WISATAWAN DOMESTIK",
        sellerId = "seller-joko",
        sellerName = "Pak Joko Widodo",
        vesselName = "KM Samudera 02",
        pickupPoint = "Pos 3 Sadranan",
        items = listOf(
            OrderItem(
                id = "oi-081",
                orderId = "ord-ff-081",
                productId = "prod-tuna-01",
                namaIkan = "Tuna Sirip Kuning Super",
                qty = 4.0,
                hargaSatuan = 45_000.0,
                subtotal = 180_000.0,
                vesselName = "KM Samudera 02",
                grade = "Grade A",
                availableStockKg = 18.0,
                imageRes = R.drawable.img_tuna_super
            )
        ),
        extraFee = 5_000.0,
        totalHarga = 185_000.0,
        status = OrderStatus.SELESAI,
        kodePickup = "FF-6210",
        createdAt = "11 Okt 2026 • 08:10 WIB"
    )
)

@Preview(showBackground = true)
@Composable
private fun OrderHistoryScreenPreview() {
    OrderHistoryScreen()
}
