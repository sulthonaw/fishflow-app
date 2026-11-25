package com.fishflow.app.feature.seller.inventory.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StockIndicator(
    stokTersedia: Double,
    modifier: Modifier = Modifier
) {
    val (statusText, statusColor) = when {
        stokTersedia <= 3.0 -> "Stok Menipis" to Color(0xFFE53935)
        stokTersedia <= 10.0 -> "Stok Sedang" to Color(0xFFFFA000)
        else -> "Stok Aman" to Color(0xFF2E7D32)
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .width(8.dp)
                    .height(8.dp)
                    .background(
                        color = statusColor,
                        shape = RoundedCornerShape(50)
                    )
            )

            Text(
                text = statusText,
                color = statusColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
