// FishCatalogScreen.kt
// Developer: Octavia
//
// Etalase/katalog ikan untuk wisatawan. Bisa dibuka tanpa login.
//
// UI (sesuai Figma "Explore Katalog"):
// - Top bar berlogo dan bottom navigation
// - Header hijau: judul, jumlah jenis ikan tersedia, chip filter wilayah
// - Search bar dan chip kategori (kebutuhan Fungsi 4, belum ada di Figma)
// - List kartu produk ikan: foto, nama, penjual, lokasi pengambilan, stok, harga/kg
// - Klik produk atau tombol Pesan -> ke FishDetailScreen
// - Empty state: "Produk belum tersedia"
package com.fishflow.app.feature.tourist.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fishflow.app.core.ui.components.EmptyStateView
import com.fishflow.app.core.ui.components.ErrorMessage
import com.fishflow.app.core.ui.components.FishFlowSearchBar
import com.fishflow.app.core.ui.components.FishFlowTopBar
import com.fishflow.app.core.ui.components.LoadingIndicator
import com.fishflow.app.core.ui.components.OrderDesignTokens
import com.fishflow.app.data.model.FishCategory
import com.fishflow.app.data.model.FishProduct
import com.fishflow.app.feature.tourist.catalog.components.CategoryFilterChips
import com.fishflow.app.feature.tourist.catalog.components.FishProductCard
import com.fishflow.app.feature.tourist.catalog.components.RegionFilterChips
import com.fishflow.app.feature.tourist.order_history.TouristOrderBottomBar

@Composable
fun FishCatalogScreen(
    uiState: FishCatalogUiState,
    onSearchChange: (String) -> Unit,
    onRegionFilter: (String?) -> Unit,
    onCategoryFilter: (FishCategory?) -> Unit,
    onClearFilter: () -> Unit,
    onProductClick: (FishProduct) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    onNavItemClick: (String) -> Unit = {}
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = OrderDesignTokens.CanvasBg,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = { FishFlowTopBar(title = "Explore Katalog", showBrand = true) },
        bottomBar = {
            TouristOrderBottomBar(activeTab = "Katalog", onTabClick = onNavItemClick)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(key = "hero") {
                CatalogHero(
                    uiState = uiState,
                    onRegionFilter = onRegionFilter,
                    onBack = onBack
                )
            }
            item(key = "filter") {
                CatalogFilterSection(
                    uiState = uiState,
                    onSearchChange = onSearchChange,
                    onCategoryFilter = onCategoryFilter,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            when {
                uiState.isLoading -> item(key = "loading") { LoadingIndicator() }

                uiState.errorMessage != null -> item(key = "error") {
                    ErrorMessage(message = uiState.errorMessage, onRetry = onRetry)
                }

                uiState.isEmpty -> item(key = "empty") {
                    CatalogEmptyState(uiState = uiState, onClearFilter = onClearFilter)
                }

                else -> {
                    item(key = "caption") {
                        IconLabel(
                            icon = Icons.Rounded.Verified,
                            text = "Tangkapan segar langsung dari kapal Penjual",
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                    items(uiState.products, key = { it.id }) { product ->
                        FishProductCard(
                            product = product,
                            onClick = { onProductClick(product) },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FishCatalogScreen(
    onProductClick: (FishProduct) -> Unit,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    onNavItemClick: (String) -> Unit = {},
    viewModel: FishCatalogViewModel = viewModel(factory = FishCatalogViewModel.factory())
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.load()
    }

    FishCatalogScreen(
        uiState = uiState,
        onSearchChange = viewModel::onSearchChange,
        onRegionFilter = viewModel::onRegionFilter,
        onCategoryFilter = viewModel::onCategoryFilter,
        onClearFilter = viewModel::onClearFilter,
        onProductClick = onProductClick,
        onRetry = viewModel::onRetry,
        modifier = modifier,
        onBack = onBack,
        onNavItemClick = onNavItemClick
    )
}

@Composable
private fun CatalogHero(
    uiState: FishCatalogUiState,
    onRegionFilter: (String?) -> Unit,
    onBack: (() -> Unit)?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(OrderDesignTokens.MaritimeDark)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (onBack != null) {
                IconButton(
                    onClick = onBack,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.White.copy(alpha = 0.14f),
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Kembali"
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "PASAR PESISIR",
                    color = OrderDesignTokens.MintAccent,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Katalog Ikan Segar",
                    color = Color.White,
                    fontSize = 20.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.14f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.LocationOn,
                    contentDescription = null,
                    tint = OrderDesignTokens.MintAccent,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "Di Blitar",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Jumlah hanya ditampilkan kalau datanya sudah pasti.
        if (!uiState.isLoading && uiState.errorMessage == null) {
            AvailableCountBanner(count = uiState.products.size)
        }

        RegionFilterChips(
            regions = uiState.regions,
            selectedRegion = uiState.selectedRegion,
            onSelect = onRegionFilter
        )
    }
}

@Composable
private fun AvailableCountBanner(count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.1f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.Inventory2,
            contentDescription = null,
            tint = OrderDesignTokens.MintAccent,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = OrderDesignTokens.MintAccent)) {
                    append("$count Jenis Ikan ")
                }
                append("Tersedia Hari Ini")
            },
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun CatalogFilterSection(
    uiState: FishCatalogUiState,
    onSearchChange: (String) -> Unit,
    onCategoryFilter: (FishCategory?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FishFlowSearchBar(
            query = uiState.searchQuery,
            onQueryChange = onSearchChange,
            placeholder = "Cari nama ikan..."
        )
        CategoryFilterChips(
            categories = uiState.categories,
            selectedCategory = uiState.selectedCategory,
            onSelect = onCategoryFilter
        )
    }
}

@Composable
internal fun IconLabel(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = OrderDesignTokens.CoastalTeal,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = text,
            color = OrderDesignTokens.TextSecondary,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun CatalogEmptyState(
    uiState: FishCatalogUiState,
    onClearFilter: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmptyStateView(
            message = "Produk belum tersedia",
            description = if (uiState.hasActiveFilter) {
                "Coba ubah wilayah, kategori, atau kata kunci."
            } else {
                "Silakan cek kembali nanti."
            }
        )
        if (uiState.hasActiveFilter) {
            TextButton(onClick = onClearFilter) {
                Text(text = "Hapus Filter", color = OrderDesignTokens.CoastalTeal)
            }
        }
    }
}
