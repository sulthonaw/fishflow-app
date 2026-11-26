// TodayCatchSection.kt
// Developer: Octavia
// Section "Hasil Tangkapan Hari Ini" — grid 2 kolom produk ikan sesuai Figma.
// Stateless — terima List<FishProduct> dan onProductClick.
package com.fishflow.app.feature.tourist.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fishflow.app.core.ui.components.OrderDesignTokens
import com.fishflow.app.core.ui.theme.FishFlowTheme
import com.fishflow.app.data.model.FishCategory
import com.fishflow.app.data.model.FishProduct
import com.fishflow.app.feature.tourist.catalog.components.FishProductImage
import com.fishflow.app.feature.tourist.catalog.components.ProductPrice
import com.fishflow.app.feature.tourist.catalog.components.formatStokTersedia
import com.fishflow.app.feature.tourist.catalog.components.formatWilayah

@Composable
fun TodayCatchSection(
    products: List<FishProduct>,
    onProductClick: (FishProduct) -> Unit,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    HomeGridSection(
        title = "Hasil Tangkapan Hari Ini",
        items = products,
        onSeeAllClick = onSeeAllClick,
        modifier = modifier
    ) { product, cardModifier ->
        HomeGridCard(
            name = product.namaIkan,
            location = formatWilayah(product.wilayah),
            actionIcon = Icons.Rounded.Add,
            actionDescription = "Pesan ${product.namaIkan}",
            onClick = { onProductClick(product) },
            modifier = cardModifier,
            image = { TodayCatchImage(product = product) },
            footer = { footerModifier ->
                ProductPrice(
                    hargaPerKg = product.hargaPerKg,
                    fontSize = 14,
                    modifier = footerModifier
                )
            }
        )
    }
}

@Composable
private fun TodayCatchImage(product: FishProduct) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(128.dp)
    ) {
        FishProductImage(
            namaIkan = product.namaIkan,
            imageRes = product.imageRes,
            fotoUrl = product.fotoUrl,
            modifier = Modifier.fillMaxSize()
        )
        Text(
            text = formatStokTersedia(product.stokTersedia),
            color = OrderDesignTokens.MaritimeDark,
            fontSize = 10.sp,
            lineHeight = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
                .clip(CircleShape)
                .background(OrderDesignTokens.MintAccent)
                .padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun TodayCatchSectionPreview() {
    FishFlowTheme {
        TodayCatchSection(
            products = listOf(
                FishProduct(
                    id = "preview",
                    sellerId = "seller",
                    namaIkan = "Cumi-cumi Segar",
                    kategori = FishCategory.CUMI_GURITA,
                    hargaPerKg = 55_000.0,
                    wilayah = "Serang",
                    lokasiPengambilan = "Dermaga Pantai Serang",
                    namaPenjual = "Nelayan Bahari",
                    stokTersedia = 12.5
                )
            ),
            onProductClick = {},
            onSeeAllClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
