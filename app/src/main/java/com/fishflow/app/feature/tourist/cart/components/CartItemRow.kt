// CartItemRow.kt
// Developer: Sulthon
// Satu baris item di keranjang. Stateless.
// Terima: nama ikan, qty, hargaSatuan, subtotal, stockError (nullable),
//         onUpdateQty, onRemove lambda.
// Pakai QuantityStepper untuk atur jumlah.
package com.fishflow.app.feature.tourist.cart.components

import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.DirectionsBoat
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.fishflow.app.core.ui.components.CompactQuantityStepper
import com.fishflow.app.core.ui.components.OrderDesignTokens
import com.fishflow.app.core.util.CurrencyFormatter

@Composable
fun CartItemRow(
    namaIkan: String,
    vesselName: String,
    qty: Int,
    hargaSatuan: Double,
    subtotal: Double,
    availableStockKg: Int,
    stockError: String?,
    onUpdateQty: (Int) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
    badgeLabel: String = "Segar",
    @DrawableRes imageRes: Int = R.drawable.img_tuna_super
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
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(OrderDesignTokens.SurfaceContainer)
                ) {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = namaIkan,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(OrderDesignTokens.ForestDeep)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badgeLabel,
                            color = OrderDesignTokens.MintAccent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.DirectionsBoat,
                            contentDescription = null,
                            tint = OrderDesignTokens.CoastalTeal,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = vesselName,
                            color = OrderDesignTokens.CoastalTeal,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        text = namaIkan,
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
                        Text(
                            text = CurrencyFormatter.formatRupiah(hargaSatuan),
                            color = OrderDesignTokens.CoastalTeal,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "/ kg",
                            color = OrderDesignTokens.TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    Text(
                        text = "Stok live: $availableStockKg kg",
                        color = OrderDesignTokens.TextMuted,
                        fontSize = 11.sp
                    )
                }

                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.DeleteOutline,
                        contentDescription = "Hapus item",
                        tint = OrderDesignTokens.ErrorText,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CompactQuantityStepper(
                    quantity = qty,
                    onIncrease = { onUpdateQty(qty + 1) },
                    onDecrease = { if (qty > 1) onUpdateQty(qty - 1) },
                    maxQuantity = maxOf(availableStockKg, qty) + 1
                )

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Subtotal",
                        color = OrderDesignTokens.TextMuted,
                        fontSize = 11.sp
                    )
                    Text(
                        text = CurrencyFormatter.formatRupiah(subtotal),
                        color = OrderDesignTokens.ForestDeep,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (!stockError.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(OrderDesignTokens.ErrorBg)
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ErrorOutline,
                        contentDescription = null,
                        tint = OrderDesignTokens.ErrorText,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = stockError,
                        color = OrderDesignTokens.ErrorText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
