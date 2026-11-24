// SellerOrderDetailScreen.kt
// Developer: Sulthon
//
// Detail satu pesanan (sisi penjual).
//
// UI:
// - Info pembeli: nama
// - Daftar item: nama ikan, qty (kg), harga satuan, subtotal
// - Total harga keseluruhan
// - Status pesanan saat ini
// - Jika status MENUNGGU → tampilkan 2 tombol: "Terima & Kunci Stok" dan "Tolak"
// - Jika status DIKONFIRMASI → tombol "Selesai Diserahkan" (setelah ikan diambil)
// - Dialog konfirmasi sebelum terima/tolak
package com.fishflow.app.feature.seller.orders

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fishflow.app.R
import com.fishflow.app.core.ui.components.OrderDesignTokens
import com.fishflow.app.core.ui.components.StatusBadge
import com.fishflow.app.core.util.CurrencyFormatter
import com.fishflow.app.data.model.Order
import com.fishflow.app.data.model.OrderStatus
import com.fishflow.app.feature.seller.orders.components.OrderActionButtons
import com.fishflow.app.feature.seller.orders.components.SellerHandoverActionButtons

private enum class DetailActionDialog { CONFIRM, REJECT, COMPLETE }

@Composable
fun SellerOrderDetailScreen(
    modifier: Modifier = Modifier,
    initialOrder: Order = SellerOrderUiState.sampleSellerOrders().first(),
    onBack: () -> Unit = {}
) {
    var order by remember { mutableStateOf(initialOrder) }
    var activeDialog by remember { mutableStateOf<DetailActionDialog?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = OrderDesignTokens.CanvasBg,
        topBar = {
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
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Kembali",
                            tint = OrderDesignTokens.TextPrimary
                        )
                    }
                    Text(
                        text = "Detail Pesanan",
                        color = OrderDesignTokens.TextPrimary,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                    .background(OrderDesignTokens.MaritimeDark)
                    .padding(horizontal = 16.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "KODE PESANAN",
                            color = OrderDesignTokens.MintAccent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = order.orderCode,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    StatusBadge(status = order.status)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.LocationOn,
                        contentDescription = null,
                        tint = OrderDesignTokens.MintAccent,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "${order.vesselName} • ${order.pickupPoint}",
                        color = OrderDesignTokens.SageLight,
                        fontSize = 12.sp
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_buyer_bima),
                            contentDescription = order.buyerName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Pembeli",
                                color = OrderDesignTokens.TextMuted,
                                fontSize = 11.sp
                            )
                            Text(
                                text = order.buyerName,
                                color = OrderDesignTokens.TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${order.buyerSegment} • ${order.createdAt}",
                                color = OrderDesignTokens.TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Daftar Item Pesanan",
                            color = OrderDesignTokens.TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )

                        order.items.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(OrderDesignTokens.SurfaceMuted)
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = item.imageRes ?: R.drawable.img_tuna_super),
                                    contentDescription = item.namaIkan,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.namaIkan,
                                        color = OrderDesignTokens.TextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "${OrderDesignTokens.formatKg(item.qty)} kg × ${CurrencyFormatter.formatRupiah(item.hargaSatuan)}",
                                        color = OrderDesignTokens.TextSecondary,
                                        fontSize = 12.sp
                                    )
                                }
                                Text(
                                    text = CurrencyFormatter.formatRupiah(item.subtotal),
                                    color = OrderDesignTokens.CoastalTeal,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (order.extraFee > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Pembersihan & Es Curah",
                                    color = OrderDesignTokens.TextSecondary,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = CurrencyFormatter.formatRupiah(order.extraFee),
                                    color = OrderDesignTokens.TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        HorizontalDivider(color = OrderDesignTokens.DividerColor)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total Harga",
                                color = OrderDesignTokens.ForestDeep,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = CurrencyFormatter.formatRupiah(order.totalHarga),
                                color = OrderDesignTokens.ForestDeep,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(OrderDesignTokens.MintAccent.copy(alpha = 0.35f))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = OrderDesignTokens.CoastalTealDark,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Stok live di cold box kapal mencukupi untuk pesanan ini.",
                        color = OrderDesignTokens.CoastalTealDark,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                when (order.status) {
                    OrderStatus.MENUNGGU_KONFIRMASI -> {
                        OrderActionButtons(
                            onConfirm = { activeDialog = DetailActionDialog.CONFIRM },
                            onReject = { activeDialog = DetailActionDialog.REJECT },
                            confirmLabel = "Terima & Kunci Stok"
                        )
                    }
                    OrderStatus.DIKONFIRMASI,
                    OrderStatus.SIAP_DIAMBIL -> {
                        SellerHandoverActionButtons(
                            onScanQr = {},
                            onCompleteHandover = { activeDialog = DetailActionDialog.COMPLETE }
                        )
                    }
                    else -> Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }

    activeDialog?.let { dialogType ->
        val (title, message, confirmLabel) = when (dialogType) {
            DetailActionDialog.CONFIRM -> Triple(
                "Konfirmasi Terima Pesanan",
                "Terima pesanan ${order.orderCode} dan kunci stok ikan sekarang?",
                "Terima & Kunci Stok"
            )
            DetailActionDialog.REJECT -> Triple(
                "Konfirmasi Tolak Pesanan",
                "Yakin ingin menolak pesanan ${order.orderCode}? Dana pembeli akan dikembalikan.",
                "Tolak Pesanan"
            )
            DetailActionDialog.COMPLETE -> Triple(
                "Selesaikan Serah Terima",
                "Tandai pesanan ${order.orderCode} sudah diserahkan kepada ${order.buyerName}?",
                "Selesai Diserahkan"
            )
        }

        AlertDialog(
            onDismissRequest = { activeDialog = null },
            title = { Text(text = title, fontWeight = FontWeight.Bold) },
            text = { Text(text = message) },
            confirmButton = {
                Button(
                    onClick = {
                        order = order.copy(
                            status = when (dialogType) {
                                DetailActionDialog.CONFIRM -> OrderStatus.DIKONFIRMASI
                                DetailActionDialog.REJECT -> OrderStatus.DITOLAK
                                DetailActionDialog.COMPLETE -> OrderStatus.SELESAI
                            }
                        )
                        activeDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (dialogType == DetailActionDialog.REJECT) {
                            OrderDesignTokens.ErrorText
                        } else {
                            OrderDesignTokens.CoastalTeal
                        }
                    )
                ) {
                    Text(text = confirmLabel, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { activeDialog = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SellerOrderDetailScreenPreview() {
    SellerOrderDetailScreen()
}
