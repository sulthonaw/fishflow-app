// CheckoutScreen.kt
// Developer: Sulthon
//
// Halaman konfirmasi dan pembayaran.
//
// UI (sesuai mockup):
// - Ringkasan pesanan: daftar item, qty, subtotal per item
// - Biaya tambahan (es curah, pembersihan)
// - Total harga besar di bawah
// - Info: "Alokasi stok terjamin setelah penjual konfirmasi"
// - Tombol "Bayar & Pesan" (prominent)
// - Tombol "Batal" (text button)
package com.fishflow.app.feature.tourist.checkout

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.AccountTree
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DirectionsBoat
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fishflow.app.R
import com.fishflow.app.core.ui.components.ErrorToastEffect
import com.fishflow.app.core.ui.components.FishFlowTopBar
import com.fishflow.app.core.ui.components.OrderDesignTokens
import com.fishflow.app.core.ui.components.QuantityStepper
import com.fishflow.app.core.util.CurrencyFormatter
import com.fishflow.app.feature.tourist.checkout.components.OrderConfirmationCard

@Composable
fun CheckoutScreen(
    modifier: Modifier = Modifier,
    initialState: CheckoutUiState = CheckoutUiState(),
    onBack: () -> Unit = {},
    onConfirmOrder: (CheckoutUiState) -> Unit = {}
) {
    ErrorToastEffect(errorMessage = initialState.errorMessage)
    var uiState by remember { mutableStateOf(initialState) }
    val primaryItem = uiState.primaryItem
    val currentQty = primaryItem?.qty?.toInt() ?: 1
    val maxStock = primaryItem?.availableStockKg?.toInt() ?: 18
    val remainingStock = (maxStock - currentQty).coerceAtLeast(0)
    val isStockAvailable = uiState.isStockSufficient

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = OrderDesignTokens.CanvasBg,
        topBar = {
            FishFlowTopBar(
                title = "Checkout Pembayaran",
                onBack = onBack
            )
        },
        bottomBar = {
            CheckoutStickyBottomBar(
                totalHarga = uiState.totalHarga,
                enabled = isStockAvailable && !uiState.isProcessing,
                onConfirm = { onConfirmOrder(uiState) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            CheckoutCoastalHero(
                buyerName = uiState.buyerName,
                buyerPhone = uiState.buyerPhone,
                isVerified = uiState.isBuyerVerified
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-14).dp)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (primaryItem != null) {
                    SelectedCatchCard(
                        vesselName = primaryItem.vesselName,
                        fishName = primaryItem.namaIkan,
                        pricePerKg = primaryItem.hargaSatuan,
                        availableStockKg = maxStock,
                        imageRes = primaryItem.imageRes ?: R.drawable.img_tuna_super
                    )
                }

                QuantityAndStockValidationCard(
                    quantity = currentQty,
                    maxStock = maxStock,
                    remainingStock = remainingStock,
                    isStockAvailable = isStockAvailable,
                    onUpdateQuantity = { nextQty ->
                        val updatedItems = uiState.orderItems.mapIndexed { idx, item ->
                            if (idx == 0) {
                                item.copy(
                                    qty = nextQty.toDouble(),
                                    subtotal = nextQty * item.hargaSatuan
                                )
                            } else {
                                item
                            }
                        }
                        uiState = uiState.copy(orderItems = updatedItems)
                    }
                )

                OrderConfirmationCard(
                    items = uiState.orderItems,
                    biayaTambahan = uiState.biayaTambahan,
                    totalHarga = uiState.totalHarga
                )

                HarborPickupCalloutCard(
                    pickupPoint = uiState.pickupPoint,
                    pickupDeadline = uiState.pickupDeadline
                )

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    TextButton(onClick = onBack) {
                        Text(
                            text = "Batal",
                            color = OrderDesignTokens.TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun CheckoutCoastalHero(
    buyerName: String,
    buyerPhone: String,
    isVerified: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(OrderDesignTokens.MaritimeDark)
            .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 36.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(9999.dp))
                            .background(OrderDesignTokens.MintAccent.copy(alpha = 0.2f))
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "TAHAP 1: VALIDASI STOK",
                            color = OrderDesignTokens.MintAccent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Text(
                        text = "Konfirmasi Pesanan",
                        color = Color.White,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.19).sp
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ReceiptLong,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.1f))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(OrderDesignTokens.MintAccent)
                    )
                    Text(
                        text = buildAnnotatedString {
                            append("$buyerName ")
                            withStyle(SpanStyle(color = OrderDesignTokens.SageMuted)) {
                                append("($buyerPhone)")
                            }
                        },
                        color = Color.White,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (isVerified) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(9999.dp))
                            .background(OrderDesignTokens.ForestDeep.copy(alpha = 0.4f))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Verified,
                            contentDescription = null,
                            tint = OrderDesignTokens.MintAccent,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "Terverifikasi",
                            color = OrderDesignTokens.MintAccent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.4.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectedCatchCard(
    vesselName: String,
    fishName: String,
    pricePerKg: Double,
    availableStockKg: Int,
    imageRes: Int
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(OrderDesignTokens.SurfaceContainer)
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = fishName,
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
                        text = "Segar",
                        color = OrderDesignTokens.MintAccent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.DirectionsBoat,
                        contentDescription = null,
                        tint = OrderDesignTokens.CoastalTeal,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = vesselName,
                        color = OrderDesignTokens.CoastalTeal,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = fishName,
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
                        text = CurrencyFormatter.formatRupiah(pricePerKg),
                        color = OrderDesignTokens.CoastalTeal,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "/ kg",
                        color = OrderDesignTokens.TextSecondary,
                        fontSize = 12.sp
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Inventory2,
                        contentDescription = null,
                        tint = OrderDesignTokens.TextSecondary,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = buildAnnotatedString {
                            append("Stok live: ")
                            withStyle(SpanStyle(color = OrderDesignTokens.TextPrimary, fontWeight = FontWeight.Medium)) {
                                append("$availableStockKg")
                            }
                            append(" kg")
                        },
                        color = OrderDesignTokens.TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun QuantityAndStockValidationCard(
    quantity: Int,
    maxStock: Int,
    remainingStock: Int,
    isStockAvailable: Boolean,
    onUpdateQuantity: (Int) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
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
                        text = "Jumlah Pesanan",
                        color = OrderDesignTokens.TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Satuan minimal 1 kilogram",
                        color = OrderDesignTokens.TextSecondary,
                        fontSize = 12.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(OrderDesignTokens.SurfaceContainer)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "Maks $maxStock kg",
                        color = OrderDesignTokens.TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.4.sp
                    )
                }
            }

            QuantityStepper(
                quantity = quantity,
                onIncrease = { if (quantity < maxStock) onUpdateQuantity(quantity + 1) },
                onDecrease = { if (quantity > 1) onUpdateQuantity(quantity - 1) },
                maxQuantity = maxStock
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isStockAvailable) {
                            OrderDesignTokens.MintAccent.copy(alpha = 0.3f)
                        } else {
                            OrderDesignTokens.ErrorBg
                        }
                    )
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(if (isStockAvailable) OrderDesignTokens.CoastalTeal else OrderDesignTokens.ErrorText),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isStockAvailable) Icons.Rounded.Check else Icons.Rounded.ErrorOutline,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = if (isStockAvailable) {
                            "Stok Tersedia & Siap Dialokasikan"
                        } else {
                            "Stok Melebihi Kuota Kapal"
                        },
                        color = if (isStockAvailable) OrderDesignTokens.CoastalTealDark else OrderDesignTokens.ErrorText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = if (isStockAvailable) {
                            "Sisa $remainingStock kg di sistem setelah pesanan Anda diproses."
                        } else {
                            "Maksimal kuota tersedia adalah $maxStock kg."
                        },
                        color = if (isStockAvailable) OrderDesignTokens.CoastalTealDark else OrderDesignTokens.ErrorText,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(OrderDesignTokens.SurfaceMuted)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Rounded.AccountTree,
                    contentDescription = null,
                    tint = OrderDesignTokens.CoastalTeal,
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .size(16.dp)
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, color = OrderDesignTokens.TextPrimary)) {
                            append("Validasi Otomatis: ")
                        }
                        append("Pembeli memilih kuantitas → Sistem cek kuota kapal secara real-time → Jika cukup, nominal tagihan langsung terhitung.")
                    },
                    color = OrderDesignTokens.TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 19.sp
                )
            }
        }
    }
}

@Composable
private fun HarborPickupCalloutCard(
    pickupPoint: String,
    pickupDeadline: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(OrderDesignTokens.MintAccent.copy(alpha = 0.2f))
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(OrderDesignTokens.CoastalTeal),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.LocationOn,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Titik Ambil: $pickupPoint",
                color = OrderDesignTokens.CoastalTealDark,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Batas waktu pengambilan pukul $pickupDeadline. Alokasi stok terjamin setelah penjual konfirmasi. Status pesanan otomatis disimpan sebagai:",
                color = OrderDesignTokens.CoastalTealDark,
                fontSize = 12.sp,
                lineHeight = 16.5.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.White,
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(OrderDesignTokens.WarningDot)
                    )
                    Text(
                        text = buildAnnotatedString {
                            append("Status Awal: ")
                            withStyle(SpanStyle(color = OrderDesignTokens.ForestDeep, fontWeight = FontWeight.Medium)) {
                                append("Menunggu Konfirmasi Penjual")
                            }
                        },
                        color = OrderDesignTokens.TextPrimary,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun CheckoutStickyBottomBar(
    totalHarga: Double,
    enabled: Boolean,
    onConfirm: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White.copy(alpha = 0.96f),
        shadowElevation = 12.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Total Akhir",
                    color = OrderDesignTokens.TextSecondary,
                    fontSize = 12.sp
                )
                Text(
                    text = CurrencyFormatter.formatRupiah(totalHarga),
                    color = OrderDesignTokens.ForestDeep,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.47).sp
                )
            }

            Button(
                onClick = onConfirm,
                enabled = enabled,
                shape = RoundedCornerShape(9999.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrderDesignTokens.CoastalTeal,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                contentPadding = PaddingValues(horizontal = 20.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Text(
                    text = "Bayar & Pesan",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CheckoutScreenPreview() {
    CheckoutScreen()
}
