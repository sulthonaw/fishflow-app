// TouristHomeScreen.kt
// Developer: Octavia
//
// Beranda utama wisatawan.
//
// UI (sesuai Figma "Home"):
// - Header: sapaan, lokasi (Pantai Serang, Blitar), judul, search bar
// - Kategori cepat: chip "Ikan Segar" dan "Wisata Bahari", mengganti section di bawahnya
// - Kartu estimasi wisata (menuju daftar destinasi Fungsi 3)
// - Hasil tangkapan hari ini atau destinasi wisata (grid 2 kolom)
// - Bottom nav: Home, Katalog, Rencana, Pesanan, Profil
package com.fishflow.app.feature.tourist.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Sailing
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fishflow.app.core.ui.components.EmptyStateView
import com.fishflow.app.core.ui.components.ErrorMessage
import com.fishflow.app.core.ui.components.FishFlowBottomBar
import com.fishflow.app.core.ui.components.FishFlowProfileImage
import com.fishflow.app.core.ui.components.FishFlowSearchBar
import com.fishflow.app.core.ui.components.FishFlowTopBar
import com.fishflow.app.core.ui.components.LoadingIndicator
import com.fishflow.app.core.ui.components.OrderDesignTokens
import com.fishflow.app.core.ui.components.TouristBottomBarItems
import com.fishflow.app.core.ui.theme.FishFlowTheme
import com.fishflow.app.core.ui.components.ErrorToastEffect
import com.fishflow.app.data.model.Destination
import com.fishflow.app.data.model.FishProduct
import com.fishflow.app.feature.tourist.home.components.CategoryChips
import com.fishflow.app.feature.tourist.home.components.DestinationCarousel
import com.fishflow.app.feature.tourist.home.components.TodayCatchSection

@Composable
fun TouristHomeScreen(
    uiState: TouristHomeUiState,
    onSearchChange: (String) -> Unit,
    onSearch: () -> Unit,
    onCategoryClick: (String) -> Unit,
    onTourismClick: () -> Unit,
    onSeeAllClick: () -> Unit,
    onProductClick: (FishProduct) -> Unit,
    onDestinationClick: (Destination) -> Unit,
    onRetry: () -> Unit,
    onNavItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ErrorToastEffect(errorMessage = uiState.errorMessage ?: uiState.destinationErrorMessage)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = OrderDesignTokens.CanvasBg,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = { FishFlowTopBar(title = "Home", showBrand = true) },
        bottomBar = {
            FishFlowBottomBar(
                items = TouristBottomBarItems,
                currentRoute = "Home",
                onNavigate = onNavItemClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Latar gelap diteruskan ke belakang sudut membulat bagian isi.
            Column(modifier = Modifier.background(OrderDesignTokens.MaritimeDark)) {
                HomeHero(
                    uiState = uiState,
                    onSearchChange = onSearchChange,
                    onSearch = onSearch,
                    onCategoryClick = onCategoryClick
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                        .background(OrderDesignTokens.CanvasBg)
                        .padding(start = 16.dp, top = 24.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TripEstimateCard(onClick = onTourismClick)
                    if (uiState.selectedCategory == TouristHomeUiState.CATEGORY_TOURISM) {
                        DestinationContent(
                            uiState = uiState,
                            onDestinationClick = onDestinationClick,
                            onSeeAllClick = onTourismClick,
                            onRetry = onRetry
                        )
                    } else {
                        TodayCatchContent(
                            uiState = uiState,
                            onProductClick = onProductClick,
                            onSeeAllClick = onSeeAllClick,
                            onRetry = onRetry
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TodayCatchContent(
    uiState: TouristHomeUiState,
    onProductClick: (FishProduct) -> Unit,
    onSeeAllClick: () -> Unit,
    onRetry: () -> Unit
) {
    when {
        uiState.isLoading -> LoadingIndicator()

        uiState.errorMessage != null -> ErrorMessage(
            message = uiState.errorMessage,
            onRetry = onRetry
        )

        uiState.isEmpty -> EmptyStateView(message = "Produk belum tersedia")

        else -> TodayCatchSection(
            products = uiState.todayCatches,
            onProductClick = onProductClick,
            onSeeAllClick = onSeeAllClick
        )
    }
}

@Composable
private fun DestinationContent(
    uiState: TouristHomeUiState,
    onDestinationClick: (Destination) -> Unit,
    onSeeAllClick: () -> Unit,
    onRetry: () -> Unit
) {
    when {
        uiState.isDestinationLoading -> LoadingIndicator()

        uiState.destinationErrorMessage != null -> ErrorMessage(
            message = uiState.destinationErrorMessage,
            onRetry = onRetry
        )

        uiState.isDestinationEmpty -> EmptyStateView(message = "Destinasi belum tersedia")

        else -> DestinationCarousel(
            destinations = uiState.featuredDestinations,
            onItemClick = onDestinationClick,
            onSeeAllClick = onSeeAllClick
        )
    }
}

@Composable
fun TouristHomeScreen(
    onSearch: (String) -> Unit,
    onTourismClick: () -> Unit,
    onSeeAllClick: () -> Unit,
    onProductClick: (FishProduct) -> Unit,
    onDestinationClick: (Destination) -> Unit,
    onNavItemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TouristHomeViewModel = viewModel(factory = TouristHomeViewModel.factory())
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.onRefresh()
    }

    TouristHomeScreen(
        uiState = uiState,
        onSearchChange = viewModel::onSearchChange,
        onSearch = { onSearch(uiState.searchQuery) },
        onCategoryClick = viewModel::onCategoryClick,
        onTourismClick = onTourismClick,
        onSeeAllClick = onSeeAllClick,
        onProductClick = onProductClick,
        onDestinationClick = onDestinationClick,
        onRetry = viewModel::onRefresh,
        onNavItemClick = onNavItemClick,
        modifier = modifier
    )
}

@Composable
private fun HomeHero(
    uiState: TouristHomeUiState,
    onSearchChange: (String) -> Unit,
    onSearch: () -> Unit,
    onCategoryClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FishFlowProfileImage()
            Column {
                Text(
                    text = "Hi, Wisatawan",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.LocationOn,
                        contentDescription = null,
                        tint = OrderDesignTokens.SageLight,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "Pantai Serang, Blitar",
                        color = OrderDesignTokens.SageLight,
                        fontSize = 12.sp
                    )
                }
            }
        }
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "JELAJAH PESISIR & HASIL LAUT",
                color = OrderDesignTokens.MintAccent,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "Fresh Catch & Ocean Journey",
                color = Color.White,
                fontSize = 22.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }
        FishFlowSearchBar(
            query = uiState.searchQuery,
            onQueryChange = onSearchChange,
            modifier = Modifier.padding(horizontal = 16.dp),
            placeholder = "Cari ikan segar atau pantai...",
            onSearch = onSearch
        )
        CategoryChips(
            categories = uiState.categories,
            selectedCategory = uiState.selectedCategory,
            onCategoryClick = onCategoryClick
        )
    }
}

@Composable
private fun TripEstimateCard(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = OrderDesignTokens.MintAccent
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Estimasi Wisata Bahari",
                    color = OrderDesignTokens.TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Rencanakan trip laut, pasang ombak, dan sewa perahu Penjual lokal.",
                    color = OrderDesignTokens.TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Hitung Rencana Trip",
                        color = OrderDesignTokens.CoastalTeal,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null,
                        tint = OrderDesignTokens.CoastalTeal,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Sailing,
                    contentDescription = null,
                    tint = OrderDesignTokens.CoastalTeal,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TouristHomeScreenPreview() {
    FishFlowTheme {
        TouristHomeScreen(
            uiState = TouristHomeUiState(isLoading = false),
            onSearchChange = {},
            onSearch = {},
            onCategoryClick = {},
            onTourismClick = {},
            onSeeAllClick = {},
            onProductClick = {},
            onDestinationClick = {},
            onRetry = {},
            onNavItemClick = {}
        )
    }
}
