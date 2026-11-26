
package com.fishflow.app.feature.seller.inventory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import com.fishflow.app.data.model.FishProduct
import com.fishflow.app.data.model.ProductStatus
import com.fishflow.app.feature.seller.inventory.components.StockIndicator
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    product: FishProduct,
    viewModel: ProductDetailViewModel,
    onBack: () -> Unit,
    onUpdatePriceClick: (FishProduct) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(product.id) {
        viewModel.loadProduct(product.id)
    }

    val currencyFormat = NumberFormat.getCurrencyInstance(
        Locale("id", "ID")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Detail Produk")
                },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("← Kembali")
                    }
                }
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
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Memuat detail produk...")
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
                        color = MaterialTheme.colorScheme.error
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            viewModel.loadProduct(product.id)
                        }
                    ) {
                        Text("Coba Lagi")
                    }
                }
            }

            uiState.product != null -> {
                val currentProduct = uiState.product!!

                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "🐟",
                                fontSize = 48.sp
                            )

                            Text(
                                text = currentProduct.namaIkan,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = currentProduct.wilayah,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            val isActive = currentProduct.status == ProductStatus.AKTIF
                            Text(
                                text = if (isActive) {
                                    "● Produk Aktif"
                                } else {
                                    "● Produk Nonaktif"
                                },
                                color = if (isActive) {
                                    Color(0xFF2E7D32)
                                } else {
                                    Color(0xFFC62828)
                                },
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                text = "Harga Saat Ini",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = currencyFormat.format(
                                    currentProduct.hargaPerKg
                                ) + " / kg",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    onUpdatePriceClick(currentProduct)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Perbarui Harga")
                            }
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Informasi Stok",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            StockIndicator(
                                stokTersedia = currentProduct.stokRiil
                            )

                            StockInfoRow(
                                label = "Stok tersedia",
                                value = "${currentProduct.stokTersedia} kg"
                            )

                            StockInfoRow(
                                label = "Stok teralokasi",
                                value = "${currentProduct.stokTeralokasi} kg"
                            )

                            StockInfoRow(
                                label = "Stok riil",
                                value = "${currentProduct.stokRiil} kg",
                                isHighlighted = true
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Informasi Produk",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            StockInfoRow(
                                label = "ID Produk",
                                value = currentProduct.id
                            )

                            StockInfoRow(
                                label = "ID Seller",
                                value = currentProduct.sellerId
                            )

                            StockInfoRow(
                                label = "Wilayah",
                                value = currentProduct.wilayah
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
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
                    Text("Produk tidak ditemukan")
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = onBack) {
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
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = value,
            fontWeight = if (isHighlighted) {
                FontWeight.Bold
            } else {
                FontWeight.Medium
            },
            color = if (isHighlighted) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    }
}
