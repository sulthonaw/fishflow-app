// FishProductCard.kt
// Developer: Octavia
// Kartu produk ikan di katalog. Stateless.
// Tampilkan: foto + kategori, nama ikan, penjual, lokasi pengambilan, stok tersedia,
// harga/kg, dan tombol Pesan. Terima onClick lambda.
package com.fishflow.app.feature.tourist.catalog.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fishflow.app.core.ui.components.OrderDesignTokens
import com.fishflow.app.core.util.Constants
import com.fishflow.app.core.util.CurrencyFormatter
import com.fishflow.app.data.model.FishCategory
import com.fishflow.app.data.model.FishProduct

@Composable
fun FishProductCard(
    product: FishProduct,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProductThumbnail(product = product)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = product.namaIkan,
                    color = OrderDesignTokens.TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                SellerInfoBadge(
                    namaPenjual = product.namaPenjual,
                    namaKapal = product.namaKapal
                )
                PickupLocationLabel(lokasi = product.lokasiPengambilan)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        StockPill(stokKg = product.stokTersedia)
                        ProductPrice(hargaPerKg = product.hargaPerKg, fontSize = 15)
                    }
                    OrderButton(onClick = onClick)
                }
            }
        }
    }
}

@Composable
private fun ProductThumbnail(product: FishProduct) {
    Box(
        modifier = Modifier
            .size(88.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        FishProductImage(
            namaIkan = product.namaIkan,
            imageRes = product.imageRes,
            modifier = Modifier.fillMaxSize()
        )
        Text(
            text = product.kategori.label,
            color = Color.White,
            fontSize = 9.sp,
            lineHeight = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clip(RoundedCornerShape(bottomEnd = 10.dp))
                .background(OrderDesignTokens.CoastalTeal)
                .padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
private fun StockPill(stokKg: Double) {
    Text(
        text = formatStokTersedia(stokKg),
        color = OrderDesignTokens.MaritimeDark,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .clip(CircleShape)
            .background(OrderDesignTokens.MintAccent)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    )
}

@Composable
private fun OrderButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = CircleShape,
        contentPadding = PaddingValues(horizontal = 14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = OrderDesignTokens.CoastalTeal,
            contentColor = Color.White
        )
    ) {
        Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Text(text = "Pesan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun PickupLocationLabel(lokasi: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.LocationOn,
            contentDescription = "Lokasi pengambilan",
            tint = OrderDesignTokens.CoastalTeal,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = lokasi,
            style = MaterialTheme.typography.bodySmall,
            color = OrderDesignTokens.TextSecondary
        )
    }
}

@Composable
internal fun ProductPrice(hargaPerKg: Double, fontSize: Int, modifier: Modifier = Modifier) {
    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(CurrencyFormatter.formatRupiah(hargaPerKg))
            }
            withStyle(
                SpanStyle(color = OrderDesignTokens.TextMuted, fontSize = (fontSize * 0.75f).sp)
            ) {
                append(" /${Constants.WEIGHT_UNIT_KG}")
            }
        },
        color = OrderDesignTokens.ForestDeep,
        fontSize = fontSize.sp,
        modifier = modifier
    )
}

// Produk tanpa foto memakai inisial nama ikan.
@Composable
internal fun FishProductImage(
    namaIkan: String,
    @DrawableRes imageRes: Int?,
    modifier: Modifier = Modifier
) {
    if (imageRes != null) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = namaIkan,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    } else {
        Box(
            modifier = modifier.background(OrderDesignTokens.MintAccent),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = namaIkan.take(1).uppercase(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = OrderDesignTokens.MaritimeDark
            )
        }
    }
}

internal fun formatStokKg(stokKg: Double): String =
    "${OrderDesignTokens.formatKg(stokKg)} ${Constants.WEIGHT_UNIT_KG}"

internal fun formatStokTersedia(stokKg: Double): String =
    "Stok tersedia: ${formatStokKg(stokKg)}"

@Preview(showBackground = true, widthDp = 380)
@Composable
private fun FishProductCardPreview() {
    MaterialTheme {
        FishProductCard(
            product = FishProduct(
                id = "preview",
                sellerId = "seller",
                namaIkan = "Cumi-cumi Segar",
                kategori = FishCategory.CUMI_GURITA,
                hargaPerKg = 55_000.0,
                wilayah = "Serang",
                lokasiPengambilan = "Dermaga Pantai Serang",
                namaPenjual = "Nelayan Bahari",
                namaKapal = "KM Bahari 02",
                stokTersedia = 12.5
            ),
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
