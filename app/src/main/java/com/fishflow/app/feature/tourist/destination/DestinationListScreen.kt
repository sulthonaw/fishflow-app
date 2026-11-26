package com.fishflow.app.feature.tourist.destination

import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fishflow.app.core.ui.components.CoastalHeroHeader
import com.fishflow.app.core.ui.components.FishFlowBottomBar
import com.fishflow.app.core.ui.components.FishFlowTopBar
import com.fishflow.app.core.ui.components.OrderDesignTokens
import com.fishflow.app.core.ui.components.TouristBottomBarItems
import com.fishflow.app.data.model.Destination
import com.fishflow.app.feature.tourist.destination.components.DestinationCard

@Composable
fun DestinationListScreen(
    uiState: DestinationListUiState,
    onQueryChange: (String) -> Unit,
    onRegionFilter: (String?) -> Unit,
    onDestinationClick: (Destination) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    onNavItemClick: ((String) -> Unit)? = null
) {
    com.fishflow.app.core.ui.components.ErrorToastEffect(errorMessage = uiState.errorMessage)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = OrderDesignTokens.CanvasBg,
        topBar = {
            FishFlowTopBar(
                title = "Destinasi Wisata",
                showBrand = onBack == null,
                onBack = onBack
            )
        },
        bottomBar = {
            if (onNavItemClick != null) {
                FishFlowBottomBar(
                    items = TouristBottomBarItems,
                    currentRoute = "Rencana",
                    onNavigate = onNavItemClick
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            CoastalHeroHeader(
                badgeText = "Rencana Wisata Bahari",
                title = "Destinasi Wisata",
                subtitle = "Pilih destinasi pantai dan rencanakan aktivitas liburan Anda."
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = uiState.query,
                    onValueChange = onQueryChange,
                    placeholder = { Text("Cari destinasi...") },
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
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                if (uiState.wilayahOptions.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = uiState.selectedWilayah == null,
                            onClick = { onRegionFilter(null) },
                            label = { Text("Semua") }
                        )
                        uiState.wilayahOptions.forEach { wilayah ->
                            FilterChip(
                                selected = uiState.selectedWilayah == wilayah,
                                onClick = { onRegionFilter(wilayah) },
                                label = { Text(wilayah) }
                            )
                        }
                    }
                }

                when {
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = OrderDesignTokens.CoastalTeal)
                        }
                    }

                    uiState.errorMessage != null -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = uiState.errorMessage,
                                color = OrderDesignTokens.ErrorText,
                                fontSize = 13.sp
                            )
                            Spacer(Modifier.height(10.dp))
                            Button(
                                onClick = onRetry,
                                shape = RoundedCornerShape(9999.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = OrderDesignTokens.CoastalTeal)
                            ) {
                                Text("Coba lagi")
                            }
                        }
                    }

                    uiState.filteredDestinations.isEmpty() -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Destinasi tidak ditemukan",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = OrderDesignTokens.ForestDeep
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Coba ubah kata kunci atau filter wilayah.",
                                fontSize = 13.sp,
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
                            items(uiState.filteredDestinations, key = { it.id }) { destination ->
                                DestinationCard(
                                    destination = destination,
                                    onClick = { onDestinationClick(destination) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DestinationListScreen(
    onDestinationClick: (Destination) -> Unit,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    onNavItemClick: ((String) -> Unit)? = null,
    viewModel: DestinationListViewModel = viewModel(factory = DestinationListViewModel.factory())
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    DestinationListScreen(
        uiState = uiState,
        onQueryChange = viewModel::onSearchChange,
        onRegionFilter = viewModel::onFilterRegion,
        onDestinationClick = onDestinationClick,
        onRetry = viewModel::onRetry,
        modifier = modifier,
        onBack = onBack,
        onNavItemClick = onNavItemClick
    )
}
