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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.fishflow.app.core.ui.components.FishFlowTopBar
import com.fishflow.app.core.ui.components.OrderDesignTokens
import com.fishflow.app.feature.tourist.budget.components.ActivitySelector
import com.fishflow.app.feature.tourist.budget.components.CostBreakdownCard
import com.fishflow.app.feature.tourist.budget.components.UnavailableCostNote

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
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = OrderDesignTokens.CanvasBg,
        topBar = {
            FishFlowTopBar(
                title = "Estimasi Anggaran",
                onBack = onChangeSelection
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            CoastalHeroHeader(
                badgeText = "Perencanaan Biaya",
                title = uiState.destinationName.ifBlank { "Estimasi Anggaran" },
                subtitle = "Centang aktivitas untuk melihat perkiraan biaya wisatamu."
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when {
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = OrderDesignTokens.CoastalTeal)
                        }
                    }

                    uiState.errorMessage != null && uiState.allActivities.isEmpty() -> {
                        Text(
                            text = uiState.errorMessage,
                            color = OrderDesignTokens.ErrorText,
                            fontSize = 13.sp
                        )
                        Button(
                            onClick = onRetry,
                            shape = RoundedCornerShape(9999.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = OrderDesignTokens.CoastalTeal)
                        ) {
                            Text("Coba lagi")
                        }
                    }

                    else -> {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            shadowElevation = 2.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Pilih Aktivitas",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OrderDesignTokens.TextPrimary
                                )
                                ActivitySelector(
                                    activities = uiState.allActivities,
                                    selectedIds = uiState.selectedActivityIds,
                                    onToggle = onActivityToggle
                                )
                            }
                        }

                        if (uiState.hasUnavailableCost) {
                            UnavailableCostNote(catatan = uiState.catatanBelumTersedia)
                        }

                        CostBreakdownCard(
                            selectedActivities = uiState.selectedActivities,
                            totalBiaya = uiState.totalBiaya
                        )

                        if (uiState.selectedCount == 0) {
                            Text(
                                text = "Belum ada aktivitas yang dipilih.",
                                fontSize = 12.sp,
                                color = OrderDesignTokens.TextMuted
                            )
                        }

                        if (uiState.errorMessage != null) {
                            Text(
                                text = uiState.errorMessage,
                                color = OrderDesignTokens.ErrorText,
                                fontSize = 12.sp
                            )
                        }

                        Button(
                            onClick = onDone,
                            shape = RoundedCornerShape(9999.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = OrderDesignTokens.CoastalTeal,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text("Selesai", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }

                        TextButton(
                            onClick = onReset,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Reset pilihan",
                                color = OrderDesignTokens.CoastalTeal,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(Modifier.height(8.dp))
                    }
                }
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
