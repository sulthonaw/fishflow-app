// HomeGridSection.kt
// Developer: Octavia
// Kerangka section beranda: judul, "Lihat Semua", dan grid 2 kolom. Stateless.
// Dipakai TodayCatchSection dan DestinationCarousel supaya tata letaknya sama.
package com.fishflow.app.feature.tourist.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fishflow.app.core.ui.components.OrderDesignTokens

private const val COLUMN_COUNT = 2

@Composable
internal fun <T> HomeGridSection(
    title: String,
    items: List<T>,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier,
    card: @Composable (item: T, modifier: Modifier) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                color = OrderDesignTokens.TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onSeeAllClick) {
                Text(text = "Lihat Semua", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
        items.chunked(COLUMN_COUNT).forEach { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { item ->
                    card(
                        item,
                        Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                }
                repeat(COLUMN_COUNT - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
internal fun HomeGridCard(
    name: String,
    location: String,
    actionIcon: ImageVector,
    actionDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    image: @Composable () -> Unit,
    footer: @Composable (modifier: Modifier) -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Column {
            image()
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = name,
                    color = OrderDesignTokens.TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.LocationOn,
                        contentDescription = null,
                        tint = OrderDesignTokens.CoastalTeal,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = location,
                        color = OrderDesignTokens.TextSecondary,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    footer(Modifier.weight(1f))
                    FilledIconButton(
                        onClick = onClick,
                        modifier = Modifier.size(32.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = OrderDesignTokens.CoastalTeal,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = actionIcon,
                            contentDescription = actionDescription,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
