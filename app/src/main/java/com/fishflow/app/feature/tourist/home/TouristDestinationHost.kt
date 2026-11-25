// TouristDestinationHost.kt
// Developer: Octavia
//
// Menyambungkan beranda dan hasil pencarian ke layar Fungsi 3 (Fatih) selama NavGraph
// belum ada: Daftar Destinasi -> Detail Destinasi -> Rencana Anggaran.
// Jika initialDestinationId diisi, alur langsung dimulai dari Detail Destinasi.
package com.fishflow.app.feature.tourist.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.fishflow.app.feature.tourist.budget.BudgetPlannerScreen
import com.fishflow.app.feature.tourist.destination.DestinationDetailScreen
import com.fishflow.app.feature.tourist.destination.DestinationListScreen

@Composable
fun TouristDestinationHost(
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
    initialDestinationId: String? = null
) {
    var destinationId by rememberSaveable { mutableStateOf(initialDestinationId) }
    var selectedActivityIds by rememberSaveable { mutableStateOf(arrayListOf<String>()) }
    // Terisi hanya saat rencana anggaran dibuka.
    var budgetDestinationName by rememberSaveable { mutableStateOf<String?>(null) }

    val currentId = destinationId
    val currentBudgetName = budgetDestinationName

    val closeDetail: () -> Unit = {
        if (initialDestinationId != null) onExit() else destinationId = null
    }
    val onBack: () -> Unit = {
        when {
            budgetDestinationName != null -> budgetDestinationName = null
            destinationId != null -> closeDetail()
            else -> onExit()
        }
    }

    BackHandler(onBack = onBack)

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when {
            currentId != null && currentBudgetName != null -> BudgetPlannerScreen(
                destinationId = currentId,
                destinationName = currentBudgetName,
                onChangeSelection = { budgetDestinationName = null },
                onDone = {
                    budgetDestinationName = null
                    closeDetail()
                },
                modifier = Modifier.safeDrawingPadding(),
                initialSelectedIds = selectedActivityIds.toSet()
            )

            currentId != null -> DestinationDetailScreen(
                destinationId = currentId,
                onPlanBudget = { destination, selectedIds ->
                    selectedActivityIds = ArrayList(selectedIds)
                    budgetDestinationName = destination.nama
                },
                onBack = onBack,
                modifier = Modifier.safeDrawingPadding(),
                initialSelectedIds = selectedActivityIds.toSet()
            )

            else -> DestinationListScreen(
                onDestinationClick = { destination ->
                    selectedActivityIds = arrayListOf()
                    destinationId = destination.id
                },
                modifier = Modifier.safeDrawingPadding(),
                onBack = onBack
            )
        }
    }
}
