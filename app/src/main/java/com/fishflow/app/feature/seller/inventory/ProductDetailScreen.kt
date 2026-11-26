package com.fishflow.app.feature.seller.inventory

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fishflow.app.core.ui.components.CoastalHeroHeader
import com.fishflow.app.core.ui.components.ErrorToastEffect
import com.fishflow.app.core.ui.components.FishFlowTopBar
import com.fishflow.app.core.ui.components.OrderDesignTokens
import com.fishflow.app.core.util.CurrencyFormatter
import com.fishflow.app.data.model.FishProduct
import com.fishflow.app.data.model.ProductStatus
import com.fishflow.app.feature.seller.inventory.components.StockIndicator

@Composable
fun ProductDetailScreen(
    product: FishProduct,
    onBack: () -> Unit,
    onUpdatePriceClick: (FishProduct) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProductDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    ErrorToastEffect(
        errorMessage = uiState.error,
        onConsumed = viewModel::clearError
    )

    LaunchedEffect(product.id) {
        viewModel.loadProduct(product.id)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = OrderDesignTokens.CanvasBg,
        topBar = {
            FishFlowTopBar(
                title = "Detail Produk",
                onBack = onBack
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = OrderDesignTokens.CoastalTeal)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Memuat detail produk...", color = OrderDesignTokens.TextSecondary)
                }
            }

            uiState.error != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = uiState.error ?: "Terjadi kesalahan",
                        color = OrderDesignTokens.ErrorText
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.loadProduct(product.id) },
                        shape = RoundedCornerShape(9999.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrderDesignTokens.CoastalTeal)
                    ) {
                        Text("Coba Lagi")
                    }
                }
            }

            uiState.product != null -> {
                val currentProduct = uiState.product!!
                val isActive = currentProduct.status == ProductStatus.AKTIF

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                ) {
                    CoastalHeroHeader(
                        badgeText = currentProduct.wilayah,
                        title = currentProduct.namaIkan,
                        subtitle = if (isActive) "Status: Produk Aktif" else "Status: Produk Nonaktif",
                        trailingContent = {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(9999.dp))
                                    .background(
                                        if (isActive) OrderDesignTokens.MintAccent else OrderDesignTokens.ErrorBg
                                    )
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (isActive) "Aktif" else "Nonaktif",
                                    color = if (isActive) {
                                        OrderDesignTokens.CoastalTealDark
                                    } else {
                                        OrderDesignTokens.ErrorText
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            shadowElevation = 2.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "Harga Saat Ini",
                                    color = OrderDesignTokens.TextSecondary,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "${CurrencyFormatter.formatRupiah(currentProduct.hargaPerKg)} / kg",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OrderDesignTokens.ForestDeep
                                )
                                Button(
                                    onClick = { onUpdatePriceClick(currentProduct) },
                                    shape = RoundedCornerShape(9999.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = OrderDesignTokens.CoastalTeal,
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                ) {
                                    Text("Perbarui Harga", fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            shadowElevation = 2.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "Informasi Stok",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OrderDesignTokens.TextPrimary
                                )
                                StockIndicator(stokTersedia = currentProduct.stokRiil)
                                StockInfoRow("Stok tersedia", "${currentProduct.stokTersedia} kg")
                                StockInfoRow("Stok teralokasi", "${currentProduct.stokTeralokasi} kg")
                                StockInfoRow("Stok riil", "${currentProduct.stokRiil} kg", isHighlighted = true)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            shadowElevation = 2.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "Informasi Produk",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OrderDesignTokens.TextPrimary
                                )
                                StockInfoRow("ID Produk", currentProduct.id)
                                StockInfoRow("ID Seller", currentProduct.sellerId)
                                StockInfoRow("Wilayah", currentProduct.wilayah)
                            }
                        }
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Produk tidak ditemukan", color = OrderDesignTokens.TextSecondary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onBack,
                        shape = RoundedCornerShape(9999.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrderDesignTokens.CoastalTeal)
                    ) {
                        Text("Kembali")
                    }
                }
            }
        }
    }
}

@Composable
private fun StockInfoRow(
    label: String,
    value: String,
    isHighlighted: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = OrderDesignTokens.TextSecondary, fontSize = 13.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Medium,
            color = if (isHighlighted) OrderDesignTokens.CoastalTeal else OrderDesignTokens.TextPrimary
        )
    }
}
