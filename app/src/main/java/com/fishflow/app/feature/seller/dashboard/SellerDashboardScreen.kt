package com.fishflow.app.feature.seller.dashboard

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
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
import com.fishflow.app.core.ui.components.FishFlowBottomBar
import com.fishflow.app.core.ui.components.FishFlowTopBar
import com.fishflow.app.core.ui.components.OrderDesignTokens
import com.fishflow.app.core.ui.components.SellerBottomBarItems
import com.fishflow.app.core.util.CurrencyFormatter
import com.fishflow.app.feature.seller.dashboard.components.QuickActionButtons
import com.fishflow.app.feature.seller.dashboard.components.StatCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerDashboardScreen(
    onTambahTangkapan: () -> Unit,
    onPerbaruiHarga: () -> Unit,
    onInventoryClick: () -> Unit = {},
    onPesananClick: () -> Unit = {},
    onNavItemClick: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: SellerDashboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    ErrorToastEffect(
        errorMessage = uiState.errorMessage,
        onConsumed = viewModel::clearError
    )

    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.loadDashboard()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = OrderDesignTokens.CanvasBg,
        topBar = {
            FishFlowTopBar(
                title = "Dashboard Penjual",
                showBrand = true
            )
        },
        bottomBar = {
            FishFlowBottomBar(
                items = SellerBottomBarItems,
                currentRoute = "Dashboard",
                onNavigate = onNavItemClick
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = viewModel::onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                CoastalHeroHeader(
                    badgeText = "Nelayan Mitra",
                    title = "Halo, ${uiState.namaUser}",
                    subtitle = "Kelola stok tangkapan dan pesanan kapal hari ini."
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {

                uiState.errorMessage?.let { error ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = OrderDesignTokens.ErrorBg),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Terjadi kesalahan",
                                style = MaterialTheme.typography.titleMedium,
                                color = OrderDesignTokens.ErrorText,
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = error, color = OrderDesignTokens.ErrorText)
                            TextButton(onClick = viewModel::clearError) {
                                Text("Tutup", color = OrderDesignTokens.ErrorText)
                            }
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Ringkasan Bisnis",
                        color = OrderDesignTokens.TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
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
                        value = CurrencyFormatter.formatRupiah(uiState.omzetTotal),
                        icon = "💰",
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Aksi Cepat",
                        color = OrderDesignTokens.TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    QuickActionButtons(
                        onTambahTangkapan = onTambahTangkapan,
                        onPerbaruiHarga = onPerbaruiHarga
                    )
                }

                Surface(
                    onClick = onInventoryClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(OrderDesignTokens.CoastalTeal.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Inventory2,
                                contentDescription = null,
                                tint = OrderDesignTokens.CoastalTeal,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Lihat Inventory",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = OrderDesignTokens.TextPrimary
                            )
                            Text(
                                text = if (uiState.inventoryPreview.isEmpty()) {
                                    "Belum ada tangkapan. Ketuk untuk mulai mengelola stok."
                                } else {
                                    "${uiState.inventoryPreview.size} produk tangkapan • ${uiState.stokAktifKg} kg stok aktif"
                                },
                                fontSize = 13.sp,
                                color = OrderDesignTokens.TextSecondary
                            )
                        }

                        Icon(
                            imageVector = Icons.Rounded.ChevronRight,
                            contentDescription = "Buka Inventory",
                            tint = OrderDesignTokens.CoastalTeal
                        )
                    }
                }

                Button(
                    onClick = onPesananClick,
                    shape = RoundedCornerShape(9999.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OrderDesignTokens.CoastalTeal,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = "Lihat Pesanan Masuk",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        }
    }
}
