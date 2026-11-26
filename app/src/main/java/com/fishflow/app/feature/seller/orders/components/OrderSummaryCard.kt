// OrderSummaryCard.kt
// Developer: Sulthon
// Ringkasan pesanan dalam bentuk kartu: daftar item, qty, subtotal, total.
// Stateless — terima List<OrderItem> dan totalHarga.
package com.fishflow.app.feature.seller.orders.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fishflow.app.R
import com.fishflow.app.core.ui.components.OrderDesignTokens
import com.fishflow.app.core.ui.components.StatusBadge
import com.fishflow.app.core.util.CurrencyFormatter
import com.fishflow.app.data.model.Order
import com.fishflow.app.data.model.OrderStatus

@Composable
fun OrderSummaryCard(
    order: Order,
    onClick: () -> Unit,
    onConfirm: () -> Unit,
    onReject: () -> Unit,
    onScanQr: () -> Unit,
    onCompleteHandover: () -> Unit,
    modifier: Modifier = Modifier
) {
    val firstItem = order.items.firstOrNull()
    val totalKg = order.items.sumOf { it.qty }
    val formattedKg = OrderDesignTokens.formatKg(totalKg)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = if (order.status == OrderStatus.SIAP_DIAMBIL) 4.dp else 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (order.status == OrderStatus.SIAP_DIAMBIL || order.status == OrderStatus.DIKONFIRMASI) {
                HandoverBuyerHeader(order = order)
            } else {
                IncomingBuyerHeader(order = order)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(OrderDesignTokens.SurfaceMuted)
                    .padding(12.dp),
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
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(OrderDesignTokens.SurfaceContainer)
                    ) {
                        Image(
                            painter = painterResource(id = firstItem?.imageRes ?: R.drawable.img_tuna_super),
                            contentDescription = firstItem?.namaIkan ?: "Ikan",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Column {
                        Text(
                            text = firstItem?.namaIkan ?: "Tangkapan Segar",
                            color = OrderDesignTokens.TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        val specText = if (order.status == OrderStatus.SIAP_DIAMBIL) {
                            "$formattedKg Kg • Siap ditimbang ulang"
                        } else {
                            "Kuantitas: $formattedKg.0 Kg (${firstItem?.grade ?: "Grade A"})"
                        }
                        Text(
                            text = specText,
                            color = OrderDesignTokens.TextSecondary,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (order.status == OrderStatus.SIAP_DIAMBIL) "Nilai Tagihan" else "Total Bayar",
                        color = OrderDesignTokens.TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = CurrencyFormatter.formatRupiah(order.totalHarga),
                        color = OrderDesignTokens.CoastalTeal,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            when (order.status) {
                OrderStatus.MENUNGGU_KONFIRMASI -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(OrderDesignTokens.MintAccent.copy(alpha = 0.4f))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = OrderDesignTokens.CoastalTealDark,
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .size(16.dp)
                        )
                        Column {
                            Text(
                                text = "Stok Sistem Terverifikasi",
                                color = OrderDesignTokens.CoastalTealDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Tersedia ${OrderDesignTokens.formatKg(firstItem?.availableStockKg ?: 18.0)} kg di cold box ${order.vesselName}. Aman dialokasikan untuk pembeli.",
                                color = OrderDesignTokens.CoastalTealDark.copy(alpha = 0.85f),
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            tint = OrderDesignTokens.TextSecondary,
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .size(14.dp)
                        )
                        Text(
                            text = "Jika ditolak, sistem otomatis membatalkan alokasi & mengembalikan dana pembeli.",
                            color = OrderDesignTokens.TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }

                    OrderActionButtons(
                        onConfirm = onConfirm,
                        onReject = onReject,
                        confirmLabel = "Terima & Kunci $formattedKg Kg Stok"
                    )
                }

                OrderStatus.DIKONFIRMASI,
                OrderStatus.SIAP_DIAMBIL -> {
                    SellerHandoverActionButtons(
                        onCompleteHandover = onCompleteHandover
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(OrderDesignTokens.SurfaceMuted)
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AccountBalanceWallet,
                            contentDescription = null,
                            tint = OrderDesignTokens.CoastalTeal,
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .size(15.dp)
                        )
                        Text(
                            text = buildAnnotatedString {
                                append("Otomatis mencatat penjualan ")
                                withStyle(SpanStyle(color = OrderDesignTokens.TextPrimary, fontWeight = FontWeight.Medium)) {
                                    append(CurrencyFormatter.formatRupiah(order.totalHarga))
                                }
                                append(" ke dompet digital Penjual dan mengubah status pesanan menjadi ")
                                withStyle(SpanStyle(color = OrderDesignTokens.TextPrimary, fontWeight = FontWeight.Medium)) {
                                    append("Selesai")
                                }
                                append(".")
                            },
                            color = OrderDesignTokens.TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }

                OrderStatus.SELESAI,
                OrderStatus.DITOLAK,
                OrderStatus.DIBATALKAN -> {
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
                                imageVector = Icons.Rounded.Inventory2,
                                contentDescription = null,
                                tint = OrderDesignTokens.TextMuted,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = order.pickupPoint,
                                color = OrderDesignTokens.TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                        StatusBadge(status = order.status)
                    }
                }
            }
        }
    }
}

@Composable
private fun IncomingBuyerHeader(order: Order) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_buyer_bima),
                contentDescription = order.buyerName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
            )
            Column {
                Text(
                    text = order.buyerName,
                    color = OrderDesignTokens.TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${order.buyerSegment} • ${order.createdAt}",
                    color = OrderDesignTokens.TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.25.sp
                )
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(9999.dp))
                .background(OrderDesignTokens.SurfaceContainer)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = order.orderCode,
                color = OrderDesignTokens.TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.4.sp
            )
        }
    }
}

@Composable
private fun HandoverBuyerHeader(order: Order) {
    val initials = order.buyerName
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercaseChar().toString() }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(OrderDesignTokens.SurfaceContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    color = OrderDesignTokens.CoastalTeal,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Column {
                Text(
                    text = order.buyerName,
                    color = OrderDesignTokens.TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "PIN: ${order.kodePickup ?: "FF-9021"}",
                    color = OrderDesignTokens.CoastalTeal,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(9999.dp))
                .background(OrderDesignTokens.SurfaceBorder)
                .padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(OrderDesignTokens.CoastalTeal)
            )
            Text(
                text = order.pickupPoint,
                color = OrderDesignTokens.TextPrimary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
