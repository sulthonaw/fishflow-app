// OrderHistoryCard.kt
// Developer: Sulthon
// Kartu satu pesanan di riwayat. Stateless.
// Tampilkan: tanggal, nama penjual, jumlah item, total harga, badge status.
package com.fishflow.app.feature.tourist.order_history.components

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.DirectionsBoat
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fishflow.app.R
import com.fishflow.app.core.ui.components.OrderDesignTokens
import com.fishflow.app.core.ui.components.StatusBadge
import com.fishflow.app.core.util.CurrencyFormatter
import com.fishflow.app.data.model.Order

@Composable
fun OrderHistoryCard(
    order: Order,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val firstItem = order.items.firstOrNull()
    val totalKg = order.items.sumOf { it.qty }
    val thumbRes = firstItem?.imageRes ?: R.drawable.img_tuna_super

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = order.orderCode,
                        color = OrderDesignTokens.CoastalTeal,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = order.createdAt,
                        color = OrderDesignTokens.TextMuted,
                        fontSize = 11.sp
                    )
                }
                StatusBadge(status = order.status)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(OrderDesignTokens.SurfaceMuted)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(OrderDesignTokens.SurfaceContainer)
                ) {
                    Image(
                        painter = painterResource(id = thumbRes),
                        contentDescription = firstItem?.namaIkan ?: "Pesanan Ikan",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = firstItem?.namaIkan ?: "Pesanan Ikan Segar",
                        color = OrderDesignTokens.TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.DirectionsBoat,
                            contentDescription = null,
                            tint = OrderDesignTokens.CoastalTeal,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "${order.sellerName} • ${order.items.size} item (${OrderDesignTokens.formatKg(totalKg)} kg)",
                            color = OrderDesignTokens.TextSecondary,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            HorizontalDivider(color = OrderDesignTokens.DividerColor, thickness = 1.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total Pembayaran",
                        color = OrderDesignTokens.TextMuted,
                        fontSize = 11.sp
                    )
                    Text(
                        text = CurrencyFormatter.formatRupiah(order.totalHarga),
                        color = OrderDesignTokens.ForestDeep,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Lacak Pesanan",
                        color = OrderDesignTokens.CoastalTeal,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        contentDescription = null,
                        tint = OrderDesignTokens.CoastalTeal,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
