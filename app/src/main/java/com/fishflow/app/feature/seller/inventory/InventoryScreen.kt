package com.fishflow.app.feature.seller.inventory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fishflow.app.core.ui.components.CoastalHeroHeader
import com.fishflow.app.core.ui.components.FishFlowBottomBar
import com.fishflow.app.core.ui.components.FishFlowTopBar
import com.fishflow.app.core.ui.components.OrderDesignTokens
import com.fishflow.app.core.ui.components.SellerBottomBarItems
import com.fishflow.app.data.model.FishProduct
import com.fishflow.app.feature.seller.inventory.components.InventoryItemCard

@Composable
fun InventoryScreen(
    onProductClick: (FishProduct) -> Unit,
    onAddCatchClick: () -> Unit,
    onNavItemClick: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: InventoryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = OrderDesignTokens.CanvasBg,
        topBar = {
            FishFlowTopBar(
                title = "Inventory Saya",
                showBrand = true
            )
        },
        bottomBar = {
            FishFlowBottomBar(
                items = SellerBottomBarItems,
                currentRoute = "Stok",
                onNavigate = onNavItemClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            CoastalHeroHeader(
                badgeText = "Manajemen Stok",
                title = "Inventory Tangkapan",
                subtitle = "Kelola ketersediaan stok dan harga ikan segar Anda.",
                trailingContent = {
                    IconButton(
                        onClick = viewModel::onRefresh,
                        enabled = !uiState.isRefreshing
                    ) {
                        if (uiState.isRefreshing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = OrderDesignTokens.MintAccent
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = "Segarkan",
                                tint = OrderDesignTokens.MintAccent
                            )
                        }
                    }
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = viewModel::onSearchQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Cari nama ikan") },
                    placeholder = { Text("Contoh: Tuna, Kakap, Kerapu") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = null,
                            tint = OrderDesignTokens.CoastalTeal
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrderDesignTokens.CoastalTeal,
                        unfocusedBorderColor = OrderDesignTokens.DividerColor,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                when {
                    uiState.isLoading -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(color = OrderDesignTokens.CoastalTeal)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Memuat inventory...", color = OrderDesignTokens.TextSecondary)
                        }
                    }

                    uiState.errorMessage != null -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = uiState.errorMessage ?: "Terjadi kesalahan",
                                color = OrderDesignTokens.ErrorText
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = viewModel::loadInventory,
                                shape = RoundedCornerShape(9999.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = OrderDesignTokens.CoastalTeal
                                )
                            ) {
                                Text("Coba Lagi")
                            }
                        }
                    }

                    uiState.isEmpty -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Belum ada produk",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = OrderDesignTokens.ForestDeep
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Tambahkan hasil tangkapan pertamamu.",
                                color = OrderDesignTokens.TextSecondary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = onAddCatchClick,
                                shape = RoundedCornerShape(9999.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = OrderDesignTokens.CoastalTeal
                                )
                            ) {
                                Text("Tambah Tangkapan")
                            }
                        }
                    }

                    uiState.isSearchEmpty -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Produk tidak ditemukan",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = OrderDesignTokens.ForestDeep
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Coba gunakan kata pencarian lain.",
                                color = OrderDesignTokens.TextSecondary
                            )
                        }
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentPadding = PaddingValues(bottom = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = uiState.filteredProducts,
                                key = { product -> product.id }
                            ) { product ->
                                InventoryItemCard(
                                    product = product,
                                    onClick = { onProductClick(product) }
                                )
                            }
                        }
                    }
                }

                if (!uiState.isLoading && !uiState.isEmpty) {
                    Button(
                        onClick = onAddCatchClick,
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
                            text = "Tambah Tangkapan",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
