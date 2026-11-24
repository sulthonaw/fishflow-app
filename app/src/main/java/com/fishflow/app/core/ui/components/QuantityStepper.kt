// QuantityStepper.kt
// Developer: Sulthon
// Komponen +/- untuk atur jumlah pesanan.
// Terima: quantity (Int), onIncrease, onDecrease, maxQuantity.
// Tombol - disabled kalau qty = 1, tombol + disabled kalau qty = max.
package com.fishflow.app.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuantityStepper(
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    maxQuantity: Int,
    modifier: Modifier = Modifier,
    minQuantity: Int = 1,
    unitLabel: String = "kg",
    subtitle: String? = "Tangkapan Pagi"
) {
    val canDecrease = quantity > minQuantity
    val canIncrease = quantity < maxQuantity

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(OrderDesignTokens.SurfaceMuted)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Surface(
            modifier = Modifier
                .size(48.dp)
                .alpha(if (canDecrease) 1f else 0.45f)
                .clip(RoundedCornerShape(12.dp))
                .clickable(enabled = canDecrease, onClick = onDecrease),
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            shadowElevation = if (canDecrease) 1.dp else 0.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Rounded.Remove,
                    contentDescription = "Kurangi jumlah",
                    tint = OrderDesignTokens.TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = quantity.toString(),
                    color = OrderDesignTokens.ForestDeep,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.64).sp,
                    lineHeight = 32.sp
                )
                Text(
                    text = unitLabel,
                    color = OrderDesignTokens.TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    color = OrderDesignTokens.CoastalTeal,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.4.sp
                )
            }
        }

        Surface(
            modifier = Modifier
                .size(48.dp)
                .alpha(if (canIncrease) 1f else 0.45f)
                .clip(RoundedCornerShape(12.dp))
                .clickable(enabled = canIncrease, onClick = onIncrease),
            shape = RoundedCornerShape(12.dp),
            color = OrderDesignTokens.CoastalTeal,
            shadowElevation = if (canIncrease) 1.dp else 0.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Tambah jumlah",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun CompactQuantityStepper(
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    maxQuantity: Int,
    modifier: Modifier = Modifier,
    minQuantity: Int = 1,
    unitLabel: String = "kg"
) {
    val canDecrease = quantity > minQuantity
    val canIncrease = quantity < maxQuantity

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(OrderDesignTokens.SurfaceMuted)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            modifier = Modifier
                .size(32.dp)
                .alpha(if (canDecrease) 1f else 0.45f)
                .clip(RoundedCornerShape(8.dp))
                .clickable(enabled = canDecrease, onClick = onDecrease),
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            shadowElevation = 1.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Rounded.Remove,
                    contentDescription = "Kurangi",
                    tint = OrderDesignTokens.TextPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Text(
            text = "$quantity $unitLabel",
            color = OrderDesignTokens.ForestDeep,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )

        Surface(
            modifier = Modifier
                .size(32.dp)
                .alpha(if (canIncrease) 1f else 0.45f)
                .clip(RoundedCornerShape(8.dp))
                .clickable(enabled = canIncrease, onClick = onIncrease),
            shape = RoundedCornerShape(8.dp),
            color = OrderDesignTokens.CoastalTeal
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Tambah",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
