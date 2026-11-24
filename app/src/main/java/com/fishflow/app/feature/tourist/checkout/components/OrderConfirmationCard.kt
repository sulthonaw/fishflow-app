// OrderConfirmationCard.kt
// Developer: Sulthon
// Kartu ringkasan pesanan sebelum bayar. Stateless.
// Tampilkan daftar item, subtotal, biaya tambahan, total.
package com.fishflow.app.feature.tourist.checkout.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fishflow.app.core.ui.components.OrderDesignTokens
import com.fishflow.app.core.util.CurrencyFormatter
import com.fishflow.app.data.model.OrderItem

@Composable
fun OrderConfirmationCard(
    items: List<OrderItem>,
    biayaTambahan: Double,
    totalHarga: Double,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Rincian Pembayaran",
                    color = OrderDesignTokens.TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.07).sp
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(OrderDesignTokens.MintAccent.copy(alpha = 0.4f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Transparan",
                        color = OrderDesignTokens.CoastalTealDark,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.4.sp
                    )
                }
            }

            items.forEach { item ->
                val qtyText = OrderDesignTokens.formatKg(item.qty)
                val label = if (items.size == 1) {
                    "Subtotal ($qtyText kg × ${CurrencyFormatter.formatRupiah(item.hargaSatuan)})"
                } else {
                    "${item.namaIkan} ($qtyText kg × ${CurrencyFormatter.formatRupiah(item.hargaSatuan)})"
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = label,
                        color = OrderDesignTokens.TextSecondary,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = CurrencyFormatter.formatRupiah(item.subtotal),
                        color = OrderDesignTokens.TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Pembersihan & Es Curah",
                        color = OrderDesignTokens.TextSecondary,
                        fontSize = 14.sp
                    )
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Biaya penanganan es curah",
                        tint = OrderDesignTokens.TextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Text(
                    text = CurrencyFormatter.formatRupiah(biayaTambahan),
                    color = OrderDesignTokens.TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(2.dp))
            HorizontalDivider(color = OrderDesignTokens.DividerColor, thickness = 1.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Total Tagihan",
                        color = OrderDesignTokens.ForestDeep,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.19).sp
                    )
                    Text(
                        text = "Tanpa biaya perantara\ntersembunyi",
                        color = OrderDesignTokens.TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
                Text(
                    text = CurrencyFormatter.formatRupiah(totalHarga),
                    color = OrderDesignTokens.ForestDeep,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.36).sp,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}
