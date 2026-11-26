// OrderTrackingScreen.kt
// Developer: Sulthon
//
// Tracking status pesanan secara real-time.
//
// UI (sesuai mockup):
// - Kartu status pengambilan di dermaga/TPI
// - Stepper visual 4 tahap:
//   1. Pesanan Diterima
//   2. Stok Dialokasikan
//   3. Siap Diambil di Dermaga
//   4. Penyerahan Selesai
// - Tahap yang sudah dilalui ditandai centang hijau
// - Kode QR/token pengambilan (muncul setelah status SIAP_DIAMBIL)
// - Info kontak penjual
// - Realtime update — status berubah otomatis tanpa refresh
package com.fishflow.app.feature.tourist.order_history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.NearMe
import androidx.compose.material.icons.rounded.Sailing
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fishflow.app.R
import com.fishflow.app.core.ui.components.FishFlowTopBar
import com.fishflow.app.core.ui.components.OrderDesignTokens
import com.fishflow.app.core.ui.components.StatusBadge
import com.fishflow.app.core.util.CurrencyFormatter
import com.fishflow.app.data.model.Order
import com.fishflow.app.data.model.OrderItem
import com.fishflow.app.data.model.OrderStatus
import com.fishflow.app.feature.tourist.order_history.components.OrderStatusStepper
import com.fishflow.app.feature.tourist.order_history.components.PickupCodeCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
    modifier: Modifier = Modifier,
    initialOrder: Order = sampleTrackingOrder(),
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
    onCancelOrder: (String) -> Unit = {},
    onBack: () -> Unit = {},
    onOpenMaps: () -> Unit = {},
    onCallSeller: () -> Unit = {},
    onWhatsAppSeller: () -> Unit = {}
) {
    var order by remember { mutableStateOf(initialOrder) }
    var showCancelDialog by remember { mutableStateOf(false) }
    androidx.compose.runtime.LaunchedEffect(initialOrder) {
        order = initialOrder
    }
    val primaryItem = order.items.firstOrNull()
    val showPickupCode = order.status == OrderStatus.SIAP_DIAMBIL || order.status == OrderStatus.SELESAI

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = OrderDesignTokens.CanvasBg,
        topBar = {
            FishFlowTopBar(
                title = "Tracking Pesanan",
                onBack = onBack
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                TrackingCoastalHero(order = order)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HarborArrivalAlertBanner(
                    vesselName = order.vesselName
                )

                OrderStatusStepper(
                    currentStatus = order.status,
                    sellerShortName = "Pak Joko",
                    allocatedSummary = "${OrderDesignTokens.formatKg(primaryItem?.qty ?: 3.0)} kg tuna segar dikunci dalam wadah bernomor #A-14.",
                    pickupPoint = order.pickupPoint
                )

                if (showPickupCode) {
                    PickupCodeCard(
                        kodePickup = order.kodePickup ?: "FF-8842",
                        sellerLabel = "Pak Joko (Penjual Sadranan)"
                    )
                }

                FishermanContactCard(
                    sellerName = order.sellerName,
                    vesselCaption = "Kapten ${order.vesselName} • ${order.pickupPoint}",
                    onCall = onCallSeller
                )

                HarborLocationMapCard(
                    pickupPoint = order.pickupPoint,
                    onOpenMaps = onOpenMaps,
                    onWhatsApp = onWhatsAppSeller
                )

                if (order.status == OrderStatus.MENUNGGU_KONFIRMASI) {
                    androidx.compose.material3.OutlinedButton(
                        onClick = { showCancelDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(9999.dp),
                        border = BorderStroke(1.dp, OrderDesignTokens.ErrorText),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = OrderDesignTokens.ErrorText
                        )
                    ) {
                        Text(
                            text = "Batalkan Pesanan",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                SatisfactionAssuranceCard()

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        }
    }

    if (showCancelDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text(text = "Batalkan Pesanan?", fontWeight = FontWeight.Bold) },
            text = {
                Text("Pesanan ${order.orderCode} akan dibatalkan dan stok ikan dikembalikan.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCancelDialog = false
                        onCancelOrder(order.id)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OrderDesignTokens.ErrorText
                    )
                ) {
                    Text("Ya, Batalkan", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Kembali")
                }
            }
        )
    }
}

@Composable
private fun TrackingCoastalHero(order: Order) {
    val firstItem = order.items.firstOrNull()
    val totalKg = order.items.sumOf { it.qty }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(OrderDesignTokens.MaritimeDark)
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column {
                Text(
                    text = "PEMANTAUAN REAL-TIME",
                    color = OrderDesignTokens.MintAccent,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Tarik ke bawah untuk memperbarui status",
                    color = OrderDesignTokens.SageLight,
                    fontSize = 12.sp
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.1f))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ReceiptLong,
                            contentDescription = null,
                            tint = OrderDesignTokens.MintAccent,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = order.orderCode,
                            color = OrderDesignTokens.MintAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    StatusBadge(status = order.status)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = firstItem?.imageRes ?: R.drawable.img_tuna_super),
                        contentDescription = firstItem?.namaIkan ?: "Ikan",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = firstItem?.namaIkan ?: "Tuna Sirip Kuning Super",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Porsi: ${OrderDesignTokens.formatKg(totalKg)} kg",
                                color = OrderDesignTokens.SageMuted,
                                fontSize = 12.sp
                            )
                            Text(
                                text = CurrencyFormatter.formatRupiah(order.totalHarga),
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HarborArrivalAlertBanner(vesselName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(OrderDesignTokens.MintAccent.copy(alpha = 0.6f))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(OrderDesignTokens.MintAccent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Sailing,
                contentDescription = null,
                tint = OrderDesignTokens.CoastalTealDark,
                modifier = Modifier.size(18.dp)
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "Perahu $vesselName Tiba!",
                color = OrderDesignTokens.CoastalTealDark,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Ikan telah ditimbang & ditempatkan dalam coolbox es sadranan. Silakan menuju dermaga.",
                color = OrderDesignTokens.CoastalTealDark.copy(alpha = 0.85f),
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun FishermanContactCard(
    sellerName: String,
    vesselCaption: String,
    onCall: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_profile_seller),
                    contentDescription = sellerName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = sellerName,
                            color = OrderDesignTokens.TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(
                            imageVector = Icons.Rounded.Verified,
                            contentDescription = null,
                            tint = OrderDesignTokens.CoastalTeal,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        text = vesselCaption,
                        color = OrderDesignTokens.TextMuted,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Star,
                            contentDescription = null,
                            tint = OrderDesignTokens.WarningDot,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "4.9 (128 ulasan)",
                            color = OrderDesignTokens.WarningText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Surface(
                shape = CircleShape,
                color = OrderDesignTokens.MintAccent,
                shadowElevation = 1.dp,
                modifier = Modifier.size(44.dp)
            ) {
                IconButton(onClick = onCall) {
                    Icon(
                        imageVector = Icons.Rounded.Call,
                        contentDescription = "Telepon Nelayan",
                        tint = OrderDesignTokens.CoastalTealDark,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun HarborLocationMapCard(
    pickupPoint: String,
    onOpenMaps: () -> Unit,
    onWhatsApp: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.LocationOn,
                        contentDescription = null,
                        tint = OrderDesignTokens.CoastalTeal,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Titik Temu: $pickupPoint",
                        color = OrderDesignTokens.TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(OrderDesignTokens.MintAccent.copy(alpha = 0.4f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "350m dari lokasi Anda",
                        color = OrderDesignTokens.CoastalTeal,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(144.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(OrderDesignTokens.SurfaceBorder)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_harbor_map),
                    contentDescription = "Peta Lokasi Dermaga",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(OrderDesignTokens.ForestDeep.copy(alpha = 0.85f))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.LocationOn,
                        contentDescription = null,
                        tint = OrderDesignTokens.MintAccent,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "Meja Timbang Penjual #03",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Button(
                onClick = onOpenMaps,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrderDesignTokens.SurfaceContainer,
                    contentColor = OrderDesignTokens.TextPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.NearMe,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "Buka Petunjuk Arah (Google Maps)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Button(
                onClick = onWhatsApp,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrderDesignTokens.CoastalTeal,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.Chat,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "Hubungi Penjual via WhatsApp",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun SatisfactionAssuranceCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(OrderDesignTokens.SurfaceMuted)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.Shield,
            contentDescription = null,
            tint = OrderDesignTokens.CoastalTeal,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = "Jaminan Kesegaran: Ikan ditimbang ulang di hadapan pembeli sebelum konfirmasi serah terima dilakukan.",
            color = OrderDesignTokens.TextSecondary,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )
    }
}

fun sampleTrackingOrder(): Order = Order(
    id = "ord-ff-092",
    orderCode = "#FF-20261013-092",
    buyerId = "buyer-bima",
    buyerName = "Bima Arya",
    buyerSegment = "WISATAWAN DOMESTIK",
    sellerId = "seller-joko",
    sellerName = "Pak Joko Widodo",
    vesselName = "KM Samudera 02",
    pickupPoint = "Pos 3 Sadranan",
    items = listOf(
        OrderItem(
            id = "oi-092",
            orderId = "ord-ff-092",
            productId = "prod-tuna-01",
            namaIkan = "Tuna Sirip Kuning Super",
            qty = 3.0,
            hargaSatuan = 45_000.0,
            subtotal = 135_000.0,
            vesselName = "KM Samudera 02",
            grade = "Grade A",
            availableStockKg = 18.0,
            imageRes = R.drawable.img_tuna_super
        )
    ),
    extraFee = 5_000.0,
    totalHarga = 140_000.0,
    status = OrderStatus.SIAP_DIAMBIL,
    kodePickup = "FF-8842",
    createdAt = "13 Okt 2026 • 07:12 WIB"
)

@Preview(showBackground = true)
@Composable
private fun OrderTrackingScreenPreview() {
    OrderTrackingScreen()
}
