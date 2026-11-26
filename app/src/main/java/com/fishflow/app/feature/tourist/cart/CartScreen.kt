// CartScreen.kt
// Developer: Sulthon
//
// Halaman keranjang belanja wisatawan.
//
// UI (sesuai mockup):
// - LazyColumn daftar item: nama ikan, qty stepper (+/-), harga satuan, subtotal
// - Error merah jika stok tidak cukup untuk item tertentu
// - Footer: total harga keseluruhan
// - Tombol "Lanjut ke Pembayaran" (disabled jika keranjang kosong atau stok tidak valid)
// - Tombol hapus per item (icon trash)
// - Empty state: "Keranjang masih kosong"
package com.fishflow.app.feature.tourist.cart

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fishflow.app.R
import com.fishflow.app.core.ui.components.FishFlowTopBar
import com.fishflow.app.core.ui.components.OrderDesignTokens
import com.fishflow.app.feature.tourist.cart.components.CartItemRow
import com.fishflow.app.feature.tourist.cart.components.CartSummaryFooter

@Composable
fun CartScreen(
    modifier: Modifier = Modifier,
    initialState: CartUiState = CartUiState(),
    onBack: () -> Unit = {},
    onBrowseCatalog: () -> Unit = {},
    onUpdateQuantity: (String, Int) -> Unit = { _, _ -> },
    onRemoveItem: (String) -> Unit = {},
    onProceedToCheckout: (CartUiState) -> Unit = {}
) {
    var uiState by remember { mutableStateOf(initialState) }
    androidx.compose.runtime.LaunchedEffect(initialState) {
        uiState = initialState
    }
    val stockErrors = uiState.stockErrors
    com.fishflow.app.core.ui.components.ErrorToastEffect(errorMessage = uiState.errorMessage)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = OrderDesignTokens.CanvasBg,
        topBar = {
            FishFlowTopBar(
                title = "Keranjang Belanja",
                onBack = onBack
            )
        },
        bottomBar = {
            if (!uiState.isEmpty) {
                CartSummaryFooter(
                    totalHarga = uiState.totalHarga,
                    enabled = uiState.isStockValid,
                    onCheckout = { onProceedToCheckout(uiState) }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            CartHeroBanner(itemCount = uiState.items.size)

            if (uiState.isLoading && uiState.isEmpty) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.CircularProgressIndicator(
                        color = OrderDesignTokens.CoastalTeal
                    )
                }
            } else if (uiState.isEmpty) {
                CartEmptyState(
                    onBrowseCatalog = onBrowseCatalog,
                    onRestoreSample = { uiState = CartUiState() }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.items, key = { it.productId }) { item ->
                        CartItemRow(
                            namaIkan = item.namaIkan,
                            vesselName = item.vesselName,
                            qty = item.qty,
                            hargaSatuan = item.hargaSatuan,
                            subtotal = item.subtotal,
                            availableStockKg = item.availableStockKg,
                            stockError = stockErrors[item.productId],
                            badgeLabel = item.badgeLabel,
                            imageRes = item.imageRes,
                            onUpdateQty = { newQty ->
                                val clamped = newQty.coerceAtLeast(1)
                                uiState = uiState.copy(
                                    items = uiState.items.map { existing ->
                                        if (existing.productId == item.productId) {
                                            existing.copy(qty = clamped)
                                        } else {
                                            existing
                                        }
                                    }
                                )
                                onUpdateQuantity(item.productId, clamped)
                            },
                            onRemove = {
                                uiState = uiState.copy(
                                    items = uiState.items.filterNot { it.productId == item.productId }
                                )
                                onRemoveItem(item.productId)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CartHeroBanner(itemCount: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(OrderDesignTokens.MaritimeDark)
            .padding(horizontal = 16.dp, vertical = 18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "PILIHAN TANGKAPAN SEGAR",
                    color = OrderDesignTokens.MintAccent,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = if (itemCount > 0) "$itemCount jenis ikan di keranjang" else "Keranjang Belanja",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.ShoppingCart,
                    contentDescription = null,
                    tint = OrderDesignTokens.MintAccent,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun CartEmptyState(
    onBrowseCatalog: () -> Unit,
    onRestoreSample: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(OrderDesignTokens.MintAccent.copy(alpha = 0.35f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.ShoppingCart,
                contentDescription = null,
                tint = OrderDesignTokens.CoastalTeal,
                modifier = Modifier.size(34.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Keranjang masih kosong",
            color = OrderDesignTokens.ForestDeep,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Pilih ikan segar langsung dari kapal nelayan sebelum stok dermaga habis.",
            color = OrderDesignTokens.TextSecondary,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                onRestoreSample()
                onBrowseCatalog()
            },
            shape = RoundedCornerShape(9999.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = OrderDesignTokens.CoastalTeal,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Jelajahi Katalog Ikan",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CartScreenPreview() {
    CartScreen()
}
