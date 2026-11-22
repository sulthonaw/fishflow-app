package com.fishflow.app.feature.tourist.destination

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fishflow.app.data.model.Destination
import com.fishflow.app.feature.tourist.destination.components.DestinationCard

// DestinationListScreen.kt
// Developer: Fatih
//
// Daftar destinasi wisata pesisir.
//
// UI:
// - Search/filter wilayah di atas
// - LazyColumn daftar destinasi: foto, nama, wilayah, deskripsi singkat
// - Klik -> ke DestinationDetailScreen
// - Empty state jika tidak ada destinasi di wilayah yang dipilih
@Composable
fun DestinationListScreen(
    uiState: DestinationListUiState,
    onQueryChange: (String) -> Unit,
    onRegionFilter: (String?) -> Unit,
    onDestinationClick: (Destination) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (onBack != null) {
            TextButton(onClick = onBack) {
                Text("Kembali")
            }
        }

        Text(
            text = "Destinasi Wisata",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Temukan pantai dan aktivitas pesisir untuk rencana liburanmu.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = uiState.query,
            onValueChange = onQueryChange,
            placeholder = { Text("Cari destinasi...") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

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

        Spacer(Modifier.height(12.dp))

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.errorMessage != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = uiState.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = onRetry) {
                        Text("Coba lagi")
                    }
                }
            }

            uiState.filteredDestinations.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Destinasi tidak ditemukan",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Coba ubah kata kunci atau filter wilayah.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
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

@Composable
fun DestinationListScreen(
    onDestinationClick: (Destination) -> Unit,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
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
        onBack = onBack
    )
}
