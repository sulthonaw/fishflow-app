package com.fishflow.app.feature.admin

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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

// ManageUsersScreen.kt
// Developer: Fatih
//
// Koneksi: Fitur admin tambahan. Fatih handle seluruh modul admin
// karena nyambung dengan data destinasi/aktivitas yang dia kelola.
//
// Daftar semua akun penjual dan wisatawan. Bisa lihat detail dan nonaktifkan akun.
@Composable
fun ManageUsersScreen(
    uiState: ManageUsersUiState,
    onQueryChange: (String) -> Unit,
    onRoleFilterChange: (AdminUserRole?) -> Unit,
    onToggleActive: (String) -> Unit,
    onResetFilter: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextButton(onClick = onBack) {
            Text("Kembali")
        }

        Text(
            text = "Kelola Akun",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "${uiState.users.size} akun terdaftar, ${uiState.activeCount} aktif.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = uiState.query,
            onValueChange = onQueryChange,
            placeholder = { Text("Cari nama atau email...") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = uiState.roleFilter == null,
                onClick = { onRoleFilterChange(null) },
                label = { Text("Semua") }
            )
            AdminUserRole.entries.forEach { role ->
                FilterChip(
                    selected = uiState.roleFilter == role,
                    onClick = { onRoleFilterChange(role) },
                    label = { Text(role.label) }
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        if (uiState.query.isNotBlank() || uiState.roleFilter != null) {
            TextButton(onClick = onResetFilter) {
                Text("Reset filter")
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                uiState.filteredUsers.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Akun tidak ditemukan",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Coba ubah kata kunci atau filter role.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.filteredUsers, key = { it.id }) { user ->
                            ManagedUserCard(
                                user = user,
                                onToggleActive = { onToggleActive(user.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ManageUsersScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ManageUsersViewModel = viewModel(factory = ManageUsersViewModel.factory())
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ManageUsersScreen(
        uiState = uiState,
        onQueryChange = viewModel::onSearchChange,
        onRoleFilterChange = viewModel::onRoleFilterChange,
        onToggleActive = viewModel::onToggleActive,
        onResetFilter = viewModel::onResetFilter,
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
private fun ManagedUserCard(
    user: ManagedUser,
    onToggleActive: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = user.nama,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = user.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = user.role.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (user.isActive) "Aktif" else "Nonaktif",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (user.isActive) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )
                Spacer(Modifier.weight(1f))
                TextButton(onClick = onToggleActive) {
                    Text(if (user.isActive) "Nonaktifkan" else "Aktifkan")
                }
            }
        }
    }
}
