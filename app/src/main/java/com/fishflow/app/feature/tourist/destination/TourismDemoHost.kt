package com.fishflow.app.feature.tourist.destination

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fishflow.app.feature.admin.AdminDashboardScreen
import com.fishflow.app.feature.admin.ManageDestinationScreen
import com.fishflow.app.feature.admin.ManageUsersScreen
import com.fishflow.app.feature.tourist.budget.BudgetPlannerScreen

// TourismDemoHost.kt
// Developer: Fatih
//
// Host demo untuk fitur F3 tanpa mengubah MainActivity/NavGraph milik Sulthon.
// Jalankan lewat Preview di Android Studio, atau panggil TourismDemoHost()
// dari activity demo terpisah saat integrasi.
private sealed interface DemoRoute {
    data object Menu : DemoRoute
    data object TouristList : DemoRoute
    data class TouristDetail(
        val destinationId: String,
        val destinationName: String,
        val selectedIds: Set<String>
    ) : DemoRoute
    data class TouristBudget(
        val destinationId: String,
        val destinationName: String,
        val selectedIds: Set<String>
    ) : DemoRoute
    data object AdminDashboard : DemoRoute
    data object ManageDestinations : DemoRoute
    data object ManageUsers : DemoRoute
}

@Composable
fun TourismDemoHost(modifier: Modifier = Modifier) {
    var route by remember { mutableStateOf<DemoRoute>(DemoRoute.Menu) }

    BackHandler(enabled = route != DemoRoute.Menu) {
        route = when (val current = route) {
            DemoRoute.Menu -> DemoRoute.Menu
            DemoRoute.TouristList -> DemoRoute.Menu
            is DemoRoute.TouristDetail -> DemoRoute.TouristList
            is DemoRoute.TouristBudget -> DemoRoute.TouristDetail(
                destinationId = current.destinationId,
                destinationName = current.destinationName,
                selectedIds = current.selectedIds
            )
            DemoRoute.AdminDashboard -> DemoRoute.Menu
            DemoRoute.ManageDestinations -> DemoRoute.AdminDashboard
            DemoRoute.ManageUsers -> DemoRoute.AdminDashboard
        }
    }

    when (val current = route) {
        DemoRoute.Menu -> DemoMenuScreen(
            onTourist = { route = DemoRoute.TouristList },
            onAdmin = { route = DemoRoute.AdminDashboard },
            modifier = modifier
        )

        DemoRoute.TouristList -> DestinationListScreen(
            onDestinationClick = { destination ->
                route = DemoRoute.TouristDetail(
                    destinationId = destination.id,
                    destinationName = destination.nama,
                    selectedIds = emptySet()
                )
            },
            modifier = modifier,
            onBack = { route = DemoRoute.Menu }
        )

        is DemoRoute.TouristDetail -> DestinationDetailScreen(
            destinationId = current.destinationId,
            initialSelectedIds = current.selectedIds,
            onPlanBudget = { destination, selectedIds ->
                route = DemoRoute.TouristBudget(
                    destinationId = destination.id,
                    destinationName = destination.nama,
                    selectedIds = selectedIds
                )
            },
            onBack = { route = DemoRoute.TouristList },
            modifier = modifier
        )

        is DemoRoute.TouristBudget -> BudgetPlannerScreen(
            destinationId = current.destinationId,
            destinationName = current.destinationName,
            initialSelectedIds = current.selectedIds,
            onChangeSelection = {
                route = DemoRoute.TouristDetail(
                    destinationId = current.destinationId,
                    destinationName = current.destinationName,
                    selectedIds = current.selectedIds
                )
            },
            onDone = { route = DemoRoute.TouristList },
            modifier = modifier
        )

        DemoRoute.AdminDashboard -> AdminDashboardScreen(
            onManageDestinations = { route = DemoRoute.ManageDestinations },
            onManageActivities = { route = DemoRoute.ManageDestinations },
            onManageUsers = { route = DemoRoute.ManageUsers },
            modifier = modifier,
            onBack = { route = DemoRoute.Menu }
        )

        DemoRoute.ManageDestinations -> ManageDestinationScreen(
            onBack = { route = DemoRoute.AdminDashboard },
            modifier = modifier
        )

        DemoRoute.ManageUsers -> ManageUsersScreen(
            onBack = { route = DemoRoute.AdminDashboard },
            modifier = modifier
        )
    }
}

@Composable
private fun DemoMenuScreen(
    onTourist: () -> Unit,
    onAdmin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "FishFlow F3",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Demo alur wisata dan admin",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onTourist,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Alur Wisatawan")
        }

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = onAdmin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Alur Admin")
        }
    }
}

@Preview(showBackground = true, widthDp = 420, heightDp = 900)
@Composable
private fun TourismDemoPreview() {
    MaterialTheme {
        TourismDemoHost()
    }
}
