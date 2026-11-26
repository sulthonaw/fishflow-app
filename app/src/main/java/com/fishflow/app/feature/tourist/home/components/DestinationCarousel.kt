// DestinationCarousel.kt
// Developer: Octavia
// Section destinasi unggulan, tampil saat chip "Wisata Bahari" dipilih.
// Tata letak sama dengan TodayCatchSection: grid 2 kolom.
// Stateless — terima List<Destination> dan onItemClick.
package com.fishflow.app.feature.tourist.home.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fishflow.app.core.ui.components.OrderDesignTokens
import com.fishflow.app.core.ui.theme.FishFlowTheme
import com.fishflow.app.data.model.Destination
import com.fishflow.app.feature.tourist.catalog.components.FishProductImage

@Composable
fun DestinationCarousel(
    destinations: List<Destination>,
    onItemClick: (Destination) -> Unit,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    HomeGridSection(
        title = "Destinasi Wisata",
        items = destinations,
        onSeeAllClick = onSeeAllClick,
        modifier = modifier
    ) { destination, cardModifier ->
        HomeGridCard(
            name = destination.nama,
            location = destination.wilayah,
            actionIcon = Icons.AutoMirrored.Rounded.ArrowForward,
            actionDescription = "Lihat ${destination.nama}",
            onClick = { onItemClick(destination) },
            modifier = cardModifier,
            image = {
                FishProductImage(
                    namaIkan = destination.nama,
                    imageRes = null,
                    fotoUrl = destination.fotoUrl,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(128.dp)
                )
            },
            footer = { footerModifier ->
                Text(
                    text = destination.deskripsi,
                    color = OrderDesignTokens.TextMuted,
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = footerModifier.padding(end = 8.dp)
                )
            }
        )
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun DestinationCarouselPreview() {
    FishFlowTheme {
        DestinationCarousel(
            destinations = listOf(
                Destination(
                    id = "preview",
                    nama = "Pantai Serang",
                    wilayah = "Serang",
                    deskripsi = "Pantai berpasir putih dengan tebing hijau dan spot foto."
                )
            ),
            onItemClick = {},
            onSeeAllClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
