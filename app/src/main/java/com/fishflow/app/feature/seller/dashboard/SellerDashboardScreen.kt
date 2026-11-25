package com.fishflow.app.feature.seller.dashboard

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fishflow.app.feature.seller.dashboard.components.QuickActionButtons
import com.fishflow.app.feature.seller.dashboard.components.StatCard
import java.text.NumberFormat
import java.util.Locale

@Composable
fun SellerDashboardScreen(
    viewModel: SellerDashboardViewModel,
    onTambahTangkapan: () -> Unit,
    onPerbaruiHarga: () -> Unit,
    onInventoryClick: () -> Unit = {},
    onPesananClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            DashboardHeader(
                namaUser = uiState.namaUser,
                onRefresh = viewModel::onRefresh
            )

            if (uiState.isLoading) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Memuat dashboard...")
                }
            }

            uiState.errorMessage?.let { error ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Terjadi kesalahan",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )

                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )

                        TextButton(
                            onClick = viewModel::clearError
                        ) {
                            Text("Tutup")
                        }
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Ringkasan Bisnis",
                    style = MaterialTheme.typography.titleLarge
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        label = "Stok Aktif",
                        value = "${uiState.stokAktifKg} kg",
                        icon = "🐟",
                        modifier = Modifier.weight(1f)
                    )

                    StatCard(
                        label = "Pesanan Baru",
                        value = "${uiState.pesananBaru}",
                        icon = "📦",
                        modifier = Modifier.weight(1f)
                    )
                }

                StatCard(
                    label = "Omzet",
                    value = formatRupiah(uiState.omzetTotal),
                    icon = "💰",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Aksi Cepat",
                    style = MaterialTheme.typography.titleLarge
                )

                QuickActionButtons(
                    onTambahTangkapan = onTambahTangkapan,
                    onPerbaruiHarga = onPerbaruiHarga
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Inventory Saya",
                        style = MaterialTheme.typography.titleLarge
                    )

                    TextButton(
                        onClick = onInventoryClick
                    ) {
                        Text("Lihat Semua")
                    }
                }

                if (uiState.inventoryPreview.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "🐟",
                                style = MaterialTheme.typography.headlineMedium
                            )

                            Text(
                                text = "Belum ada produk inventory",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = "Tambahkan tangkapan pertama kamu untuk mulai mengelola stok.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Button(
                                onClick = onTambahTangkapan
                            ) {
                                Text("Tambah Tangkapan")
                            }
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "${uiState.inventoryPreview.size} produk tersedia dalam preview.",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            TextButton(
                                onClick = onInventoryClick
                            ) {
                                Text("Buka Inventory")
                            }
                        }
                    }
                }
            }
            Button(
                onClick = onPesananClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Lihat Pesanan")
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


@Composable
private fun DashboardHeader(
    namaUser: String,
    onRefresh: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Halo, $namaUser 👋",
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "Kelola bisnis perikananmu hari ini.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        TextButton(
            onClick = onRefresh
        ) {
            Text("Refresh")
        }
    }
}

private fun formatRupiah(value: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(
        Locale("id", "ID")
    )

    formatter.maximumFractionDigits = 0
    formatter.minimumFractionDigits = 0

    return formatter.format(value)
}
