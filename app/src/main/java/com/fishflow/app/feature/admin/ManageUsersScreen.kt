package com.fishflow.app.feature.admin

import androidx.compose.foundation.background
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fishflow.app.core.ui.components.AdminBottomBarItems
import com.fishflow.app.core.ui.components.CoastalHeroHeader
import com.fishflow.app.core.ui.components.FishFlowBottomBar
import com.fishflow.app.core.ui.components.FishFlowTopBar
import com.fishflow.app.core.ui.components.OrderDesignTokens

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
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = OrderDesignTokens.CanvasBg,
        topBar = {
            FishFlowTopBar(
                title = "Kelola Akun",
                onBack = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            CoastalHeroHeader(
                badgeText = "Manajemen Pengguna",
                title = "Kelola Akun",
                subtitle = "${uiState.users.size} akun terdaftar • ${uiState.activeCount} akun aktif."
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = uiState.query,
                    onValueChange = onQueryChange,
                    placeholder = { Text("Cari nama atau email...") },
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

                if (uiState.query.isNotBlank() || uiState.roleFilter != null) {
                    TextButton(onClick = onResetFilter) {
                        Text(
                            text = "Reset filter",
                            color = OrderDesignTokens.CoastalTeal,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    when {
                        uiState.isLoading -> {
                            CircularProgressIndicator(
                                color = OrderDesignTokens.CoastalTeal,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }

                        uiState.filteredUsers.isEmpty() -> {
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Akun tidak ditemukan",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OrderDesignTokens.ForestDeep
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Coba ubah kata kunci atau filter role.",
                                    fontSize = 12.sp,
                                    color = OrderDesignTokens.TextSecondary
                                )
                            }
                        }

                        else -> {
                            LazyColumn(
                                contentPadding = PaddingValues(bottom = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
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
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = user.nama,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OrderDesignTokens.TextPrimary
                    )
                    Text(
                        text = user.email,
                        fontSize = 12.sp,
                        color = OrderDesignTokens.TextSecondary
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(OrderDesignTokens.MintAccent.copy(alpha = 0.45f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = user.role.label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = OrderDesignTokens.CoastalTealDark
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (user.isActive) "Aktif" else "Nonaktif",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (user.isActive) {
                        OrderDesignTokens.CoastalTeal
                    } else {
                        OrderDesignTokens.ErrorText
                    }
                )
                Spacer(Modifier.weight(1f))
                TextButton(onClick = onToggleActive) {
                    Text(
                        text = if (user.isActive) "Nonaktifkan" else "Aktifkan",
                        color = if (user.isActive) OrderDesignTokens.ErrorText else OrderDesignTokens.CoastalTeal,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
