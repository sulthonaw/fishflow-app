package com.fishflow.app.feature.tourist.budget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import com.fishflow.app.feature.tourist.budget.components.ActivitySelector
import com.fishflow.app.feature.tourist.budget.components.CostBreakdownCard
import com.fishflow.app.feature.tourist.budget.components.UnavailableCostNote

// BudgetPlannerScreen.kt
// Developer: Fatih
//
// Halaman estimasi anggaran wisata bahari.
//
// UI (sesuai mockup):
// - Nama destinasi di header
// - Checklist aktivitas dengan biayanya
// - Aktivitas yang biayanya belum diketahui ditandai khusus
// - Section bawah: Total estimasi anggaran (auto-update saat centang berubah)
// - Catatan untuk komponen biaya yang belum pasti
// - Tombol "Ubah Pilihan" dan "Selesai"
@Composable
fun BudgetPlannerScreen(
    uiState: BudgetPlannerUiState,
    onActivityToggle: (String) -> Unit,
    onReset: () -> Unit,
    onRetry: () -> Unit,
    onChangeSelection: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        TextButton(onClick = onChangeSelection) {
            Text("Ubah Pilihan")
        }

        Text(
            text = "Estimasi Anggaran",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = uiState.destinationName,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Centang aktivitas untuk melihat perkiraan biaya wisatamu.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(16.dp))

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.errorMessage != null && uiState.allActivities.isEmpty() -> {
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

            else -> {
                Text(
                    text = "Pilih Aktivitas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(8.dp))

                ActivitySelector(
                    activities = uiState.allActivities,
                    selectedIds = uiState.selectedActivityIds,
                    onToggle = onActivityToggle
                )

                if (uiState.hasUnavailableCost) {
                    Spacer(Modifier.height(12.dp))
                    UnavailableCostNote(catatan = uiState.catatanBelumTersedia)
                }

                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(Modifier.height(16.dp))

                CostBreakdownCard(
                    selectedActivities = uiState.selectedActivities,
                    totalBiaya = uiState.totalBiaya
                )

                if (uiState.selectedCount == 0) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Belum ada aktivitas yang dipilih.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (uiState.errorMessage != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = uiState.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = onDone,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Selesai")
                }

                TextButton(
                    onClick = onReset,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Reset pilihan")
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun BudgetPlannerScreen(
    destinationId: String,
    destinationName: String,
    onChangeSelection: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    initialSelectedIds: Set<String> = emptySet(),
    viewModel: BudgetPlannerViewModel = viewModel(
        key = "${destinationId}_${initialSelectedIds.sorted().joinToString(",")}",
        factory = BudgetPlannerViewModel.factory(
            destinationId = destinationId,
            destinationName = destinationName,
            initialSelectedIds = initialSelectedIds
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    BudgetPlannerScreen(
        uiState = uiState,
        onActivityToggle = viewModel::onActivityToggle,
        onReset = viewModel::onReset,
        onRetry = viewModel::onRetry,
        onChangeSelection = onChangeSelection,
        onDone = onDone,
        modifier = modifier
    )
}
