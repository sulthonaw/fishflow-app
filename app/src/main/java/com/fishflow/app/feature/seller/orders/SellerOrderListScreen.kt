// SellerOrderListScreen.kt
// Developer: Sulthon
//
// Daftar pesanan masuk untuk penjual.
//
// UI:
// - Tab/filter: Semua, Menunggu, Dikonfirmasi, Selesai
// - LazyColumn daftar pesanan: nama pembeli, jumlah item, total harga, status
// - Klik → ke SellerOrderDetailScreen
// - Badge notifikasi di tab "Menunggu" jika ada pesanan baru
// - Realtime update saat ada pesanan masuk
package com.fishflow.app.feature.seller.orders

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.DirectionsBoat
import androidx.compose.material.icons.rounded.Handshake
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fishflow.app.R
import com.fishflow.app.core.ui.components.OrderDesignTokens
import com.fishflow.app.core.util.CurrencyFormatter
import com.fishflow.app.data.model.Order
import com.fishflow.app.data.model.OrderStatus
import com.fishflow.app.feature.seller.orders.components.OrderSummaryCard

private enum class PendingOrderAction { CONFIRM, REJECT, COMPLETE }

@Composable
fun SellerOrderListScreen(
    modifier: Modifier = Modifier,
    initialState: SellerOrderUiState = SellerOrderUiState(),
    onBack: () -> Unit = {},
    onOrderClick: (Order) -> Unit = {},
    onNavClick: (String) -> Unit = {}
) {
    var uiState by remember { mutableStateOf(initialState) }
    var dialogOrder by remember { mutableStateOf<Pair<Order, PendingOrderAction>?>(null) }

    val incomingOrders = remember(uiState.orders, uiState.selectedFilter) {
        when (uiState.selectedFilter) {
            SellerOrderFilter.SEMUA -> uiState.orders.filter { it.status == OrderStatus.MENUNGGU_KONFIRMASI }
            SellerOrderFilter.MENUNGGU -> uiState.orders.filter { it.status == OrderStatus.MENUNGGU_KONFIRMASI }
            else -> emptyList()
        }
    }

    val handoverOrders = remember(uiState.orders, uiState.selectedFilter) {
        when (uiState.selectedFilter) {
            SellerOrderFilter.SEMUA -> uiState.orders.filter {
                it.status == OrderStatus.SIAP_DIAMBIL || it.status == OrderStatus.DIKONFIRMASI
            }
            SellerOrderFilter.DIKONFIRMASI -> uiState.orders.filter {
                it.status == OrderStatus.SIAP_DIAMBIL || it.status == OrderStatus.DIKONFIRMASI
            }
            else -> emptyList()
        }
    }

    val completedOrders = remember(uiState.orders, uiState.selectedFilter) {
        if (uiState.selectedFilter == SellerOrderFilter.SELESAI) {
            uiState.orders.filter { it.status == OrderStatus.SELESAI }
        } else {
            emptyList()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = OrderDesignTokens.CanvasBg,
        topBar = {
            SellerOrdersTopBar(onBack = onBack)
        },
        bottomBar = {
            SellerFloatingCapsuleBottomBar(
                activeItem = "Pesanan",
                onItemClick = onNavClick
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                SellerHeroSummarySection(
                    vesselName = uiState.vesselName,
                    harborName = uiState.harborName,
                    waitingCount = uiState.waitingCount,
                    readyCount = uiState.readyCount
                )
            }

            item {
                SellerFilterRow(
                    selectedFilter = uiState.selectedFilter,
                    waitingCount = uiState.waitingCount,
                    onSelectFilter = { filter ->
                        uiState = uiState.copy(selectedFilter = filter)
                    }
                )
            }

            if (incomingOrders.isNotEmpty()) {
                item {
                    SectionHeaderRow(
                        icon = Icons.Rounded.ReceiptLong,
                        title = "Pesanan Masuk",
                        badgeText = "Batas: 15 menit",
                        badgeBg = OrderDesignTokens.ErrorBg,
                        badgeContent = OrderDesignTokens.ErrorText,
                        badgeIcon = Icons.Rounded.Schedule
                    )
                }
                items(incomingOrders, key = { it.id }) { order ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        OrderSummaryCard(
                            order = order,
                            onClick = { onOrderClick(order) },
                            onConfirm = { dialogOrder = order to PendingOrderAction.CONFIRM },
                            onReject = { dialogOrder = order to PendingOrderAction.REJECT },
                            onScanQr = {},
                            onCompleteHandover = {}
                        )
                    }
                }
            }

            if (handoverOrders.isNotEmpty()) {
                item {
                    SectionHeaderRow(
                        icon = Icons.Rounded.QrCodeScanner,
                        title = "Serah Terima di Dermaga",
                        badgeText = "Pembeli Tiba",
                        badgeBg = OrderDesignTokens.MintAccent,
                        badgeContent = OrderDesignTokens.CoastalTealDark,
                        badgeIcon = null
                    )
                }
                items(handoverOrders, key = { it.id }) { order ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        OrderSummaryCard(
                            order = order,
                            onClick = { onOrderClick(order) },
                            onConfirm = {},
                            onReject = {},
                            onScanQr = { onOrderClick(order) },
                            onCompleteHandover = { dialogOrder = order to PendingOrderAction.COMPLETE }
                        )
                    }
                }
            }

            if (completedOrders.isNotEmpty()) {
                item {
                    SectionHeaderRow(
                        icon = Icons.Rounded.ReceiptLong,
                        title = "Pesanan Selesai",
                        badgeText = "${completedOrders.size} transaksi",
                        badgeBg = OrderDesignTokens.SurfaceContainer,
                        badgeContent = OrderDesignTokens.TextSecondary,
                        badgeIcon = null
                    )
                }
                items(completedOrders, key = { it.id }) { order ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        OrderSummaryCard(
                            order = order,
                            onClick = { onOrderClick(order) },
                            onConfirm = {},
                            onReject = {},
                            onScanQr = {},
                            onCompleteHandover = {}
                        )
                    }
                }
            }

            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    DailyTransactionRecapCard(
                        completedCount = maxOf(uiState.completedTodayCount, 4),
                        totalKg = if (uiState.completedTodayKg > 0) uiState.completedTodayKg else 21.5,
                        totalRevenue = if (uiState.completedTodayRevenue > 0) uiState.completedTodayRevenue else 742_000.0
                    )
                }
            }
        }
    }

    dialogOrder?.let { (targetOrder, action) ->
        val (title, body, confirmText) = when (action) {
            PendingOrderAction.CONFIRM -> Triple(
                "Terima & Kunci Stok?",
                "Pesanan ${targetOrder.orderCode} dari ${targetOrder.buyerName} akan dikonfirmasi dan stok ikan dikunci.",
                "Terima & Kunci"
            )
            PendingOrderAction.REJECT -> Triple(
                "Tolak Pesanan?",
                "Pesanan ${targetOrder.orderCode} akan dibatalkan dan dana pembeli otomatis dikembalikan.",
                "Ya, Tolak"
            )
            PendingOrderAction.COMPLETE -> Triple(
                "Konfirmasi Serah Terima?",
                "Pastikan ikan untuk ${targetOrder.buyerName} telah ditimbang ulang dan diserahkan di dermaga.",
                "Selesai Diserahkan"
            )
        }

        AlertDialog(
            onDismissRequest = { dialogOrder = null },
            title = { Text(text = title, fontWeight = FontWeight.Bold) },
            text = { Text(text = body) },
            confirmButton = {
                Button(
                    onClick = {
                        val updatedStatus = when (action) {
                            PendingOrderAction.CONFIRM -> OrderStatus.SIAP_DIAMBIL
                            PendingOrderAction.REJECT -> OrderStatus.DITOLAK
                            PendingOrderAction.COMPLETE -> OrderStatus.SELESAI
                        }
                        uiState = uiState.copy(
                            orders = uiState.orders.map { item ->
                                if (item.id == targetOrder.id) {
                                    item.copy(
                                        status = updatedStatus,
                                        kodePickup = item.kodePickup ?: "FF-8842"
                                    )
                                } else {
                                    item
                                }
                            }
                        )
                        dialogOrder = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (action == PendingOrderAction.REJECT) {
                            OrderDesignTokens.ErrorText
                        } else {
                            OrderDesignTokens.CoastalTeal
                        }
                    )
                ) {
                    Text(text = confirmText, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { dialogOrder = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun SellerOrdersTopBar(onBack: () -> Unit) {
    Surface(
        color = OrderDesignTokens.CanvasBg.copy(alpha = 0.94f),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(64.dp)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Kembali",
                        tint = OrderDesignTokens.TextPrimary
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.img_fishflow_logo),
                    contentDescription = "Fish Flow",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = "Ringkasan Pesanan",
                    color = OrderDesignTokens.TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Image(
                painter = painterResource(id = R.drawable.img_profile_buyer),
                contentDescription = "Profil Penjual",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(end = 4.dp)
                    .size(32.dp)
                    .clip(CircleShape)
            )
        }
    }
}

@Composable
private fun SellerHeroSummarySection(
    vesselName: String,
    harborName: String,
    waitingCount: Int,
    readyCount: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(OrderDesignTokens.MaritimeDark)
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.DirectionsBoat,
                        contentDescription = null,
                        tint = OrderDesignTokens.MintAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Column {
                    Text(
                        text = "ARMADA AKTIF",
                        color = OrderDesignTokens.MintAccent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = vesselName,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(9999.dp))
                    .background(OrderDesignTokens.MintAccent.copy(alpha = 0.2f))
                    .padding(horizontal = 12.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(OrderDesignTokens.MintAccent)
                )
                Text(
                    text = harborName,
                    color = OrderDesignTokens.MintAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickCounterPill(
                icon = Icons.Rounded.NotificationsActive,
                iconBg = OrderDesignTokens.WarningYellow.copy(alpha = 0.2f),
                iconTint = OrderDesignTokens.WarningYellow,
                countText = "$waitingCount Pesanan",
                countColor = OrderDesignTokens.WarningYellow,
                label = "Perlu Konfirmasi",
                modifier = Modifier.weight(1f)
            )
            QuickCounterPill(
                icon = Icons.Rounded.Handshake,
                iconBg = OrderDesignTokens.MintAccent.copy(alpha = 0.2f),
                iconTint = OrderDesignTokens.MintAccent,
                countText = "$readyCount Siap",
                countColor = OrderDesignTokens.MintAccent,
                label = "Serah Terima",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuickCounterPill(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    countText: String,
    countColor: Color,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(OrderDesignTokens.ForestDeep.copy(alpha = 0.5f))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )
        }
        Column {
            Text(
                text = countText,
                color = countColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                color = OrderDesignTokens.SageLight,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun SellerFilterRow(
    selectedFilter: SellerOrderFilter,
    waitingCount: Int,
    onSelectFilter: (SellerOrderFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SellerOrderFilter.entries.forEach { filter ->
            val isSelected = filter == selectedFilter
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(9999.dp))
                    .background(
                        if (isSelected) OrderDesignTokens.CoastalTeal else OrderDesignTokens.SurfaceContainer
                    )
                    .clickable { onSelectFilter(filter) }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = filter.label,
                    color = if (isSelected) Color.White else OrderDesignTokens.TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (filter == SellerOrderFilter.MENUNGGU && waitingCount > 0) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) OrderDesignTokens.MintAccent else OrderDesignTokens.ErrorText),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = waitingCount.toString(),
                            color = if (isSelected) OrderDesignTokens.ForestDeep else Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeaderRow(
    icon: ImageVector,
    title: String,
    badgeText: String,
    badgeBg: Color,
    badgeContent: Color,
    badgeIcon: ImageVector?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = OrderDesignTokens.CoastalTeal,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = title,
                color = OrderDesignTokens.TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.19).sp
            )
        }

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(9999.dp))
                .background(badgeBg)
                .padding(horizontal = 10.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (badgeIcon != null) {
                Icon(
                    imageVector = badgeIcon,
                    contentDescription = null,
                    tint = badgeContent,
                    modifier = Modifier.size(12.dp)
                )
            }
            Text(
                text = badgeText,
                color = badgeContent,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.4.sp
            )
        }
    }
}

@Composable
private fun DailyTransactionRecapCard(
    completedCount: Int,
    totalKg: Double,
    totalRevenue: Double
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(OrderDesignTokens.SurfaceContainer)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.ReceiptLong,
                    contentDescription = null,
                    tint = OrderDesignTokens.CoastalTeal,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column {
                Text(
                    text = "Rekap Transaksi Hari Ini",
                    color = OrderDesignTokens.TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "$completedCount Pesanan Terselesaikan • ${OrderDesignTokens.formatKg(totalKg)} Kg",
                    color = OrderDesignTokens.TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Text(
            text = CurrencyFormatter.formatRupiah(totalRevenue),
            color = OrderDesignTokens.TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun SellerFloatingCapsuleBottomBar(
    activeItem: String,
    onItemClick: (String) -> Unit
) {
    Surface(
        color = OrderDesignTokens.MaritimeDark,
        shadowElevation = 10.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(64.dp)
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SellerBottomIcon(
                icon = Icons.Rounded.ReceiptLong,
                label = "Pesanan",
                selected = activeItem == "Pesanan",
                onClick = { onItemClick("Pesanan") }
            )
            SellerBottomIcon(
                icon = Icons.Rounded.Inventory2,
                label = "Stok",
                selected = activeItem == "Stok",
                onClick = { onItemClick("Stok") }
            )
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(OrderDesignTokens.CoastalTeal)
                    .clickable { onItemClick("Scanner") },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.QrCodeScanner,
                    contentDescription = "Scanner Dermaga",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            SellerBottomIcon(
                icon = Icons.Rounded.AccountBalanceWallet,
                label = "Dompet",
                selected = activeItem == "Dompet",
                onClick = { onItemClick("Dompet") }
            )
            SellerBottomIcon(
                icon = Icons.Rounded.Person,
                label = "Profil",
                selected = activeItem == "Profil",
                onClick = { onItemClick("Profil") }
            )
        }
    }
}

@Composable
private fun SellerBottomIcon(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) OrderDesignTokens.MintAccent else OrderDesignTokens.SageMuted,
            modifier = Modifier.size(20.dp)
        )
        if (selected) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(OrderDesignTokens.MintAccent)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SellerOrderListScreenPreview() {
    SellerOrderListScreen()
}
