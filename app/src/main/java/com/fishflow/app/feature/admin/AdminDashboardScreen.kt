package com.fishflow.app.feature.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
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

// AdminDashboardScreen.kt
// Developer: Fatih
//
// Koneksi: Admin mengelola destinasi dan aktivitas wisata - data yang sama
// dengan yang Fatih tampilkan di tourist/destination dan tourist/budget.
// Jadi Fatih paham struktur datanya dan bisa bikin CRUD-nya.
//
// Dashboard admin: akses kelola destinasi, kelola aktivitas/kuliner, kelola akun.
@Composable
fun AdminDashboardScreen(
    uiState: AdminDashboardUiState,
    onRefresh: () -> Unit,
    onManageDestinations: () -> Unit,
    onManageActivities: () -> Unit,
    onManageUsers: () -> Unit,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        if (onBack != null) {
            TextButton(onClick = onBack) {
                Text("Kembali")
            }
        }

        Text(
            text = "Dashboard Admin",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Kelola konten wisata dan akun FishFlow.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(16.dp))

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdminStatCard(
                    label = "Destinasi",
                    value = uiState.totalDestinations,
                    modifier = Modifier.weight(1f)
                )
                AdminStatCard(
                    label = "Aktivitas",
                    value = uiState.totalActivities,
                    modifier = Modifier.weight(1f)
                )
                AdminStatCard(
                    label = "Akun",
                    value = uiState.totalUsers,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(16.dp))

            AdminMenuCard(
                title = "Kelola Destinasi",
                description = "Tambah, ubah, dan hapus destinasi wisata pesisir.",
                onClick = onManageDestinations
            )
            Spacer(Modifier.height(8.dp))
            AdminMenuCard(
                title = "Kelola Aktivitas & Biaya",
                description = "Atur aktivitas dan komponen biaya di setiap destinasi.",
                onClick = onManageActivities
            )
            Spacer(Modifier.height(8.dp))
            AdminMenuCard(
                title = "Kelola Akun",
                description = "Lihat daftar akun penjual dan wisatawan.",
                onClick = onManageUsers
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onRefresh,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Muat ulang statistik")
            }
        }
    }
}

@Composable
fun AdminDashboardScreen(
    onManageDestinations: () -> Unit,
    onManageActivities: () -> Unit,
    onManageUsers: () -> Unit,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    viewModel: AdminDashboardViewModel = viewModel(factory = AdminDashboardViewModel.factory())
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    AdminDashboardScreen(
        uiState = uiState,
        onRefresh = viewModel::refresh,
        onManageDestinations = onManageDestinations,
        onManageActivities = onManageActivities,
        onManageUsers = onManageUsers,
        modifier = modifier,
        onBack = onBack
    )
}

@Composable
private fun AdminStatCard(
    label: String,
    value: Int,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AdminMenuCard(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
