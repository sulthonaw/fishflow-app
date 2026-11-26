package com.fishflow.app.feature.admin

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.Landscape
import androidx.compose.material.icons.rounded.LocalActivity
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
fun AdminDashboardScreen(
    uiState: AdminDashboardUiState,
    onRefresh: () -> Unit,
    onManageDestinations: () -> Unit,
    onManageActivities: () -> Unit,
    onManageUsers: () -> Unit,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    onNavItemClick: ((String) -> Unit)? = null
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = OrderDesignTokens.CanvasBg,
        topBar = {
            FishFlowTopBar(
                title = "Dashboard Admin",
                showBrand = onBack == null,
                onBack = onBack
            )
        },
        bottomBar = {
            if (onNavItemClick != null) {
                FishFlowBottomBar(
                    items = AdminBottomBarItems,
                    currentRoute = "Dashboard",
                    onNavigate = onNavItemClick
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            CoastalHeroHeader(
                badgeText = "Panel Pengelola",
                title = "Dashboard Admin",
                subtitle = "Kelola destinasi wisata, aktivitas pesisir, dan akun pengguna FishFlow.",
                trailingContent = {
                    IconButton(onClick = onRefresh) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "Muat ulang statistik",
                            tint = OrderDesignTokens.MintAccent
                        )
                    }
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = OrderDesignTokens.CoastalTeal)
                    }
                } else {
                    if (uiState.errorMessage != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(OrderDesignTokens.ErrorBg)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = uiState.errorMessage,
                                color = OrderDesignTokens.ErrorText,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
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

                    Text(
                        text = "Menu Pengelolaan",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = OrderDesignTokens.TextPrimary
                    )

                    AdminMenuCard(
                        icon = Icons.Rounded.Landscape,
                        title = "Kelola Destinasi",
                        description = "Tambah, ubah, dan hapus destinasi wisata pesisir.",
                        onClick = onManageDestinations
                    )
                    AdminMenuCard(
                        icon = Icons.Rounded.LocalActivity,
                        title = "Kelola Aktivitas & Biaya",
                        description = "Atur aktivitas dan komponen biaya di setiap destinasi.",
                        onClick = onManageActivities
                    )
                    AdminMenuCard(
                        icon = Icons.Rounded.Group,
                        title = "Kelola Akun",
                        description = "Lihat daftar akun penjual dan wisatawan.",
                        onClick = onManageUsers
                    )
                }

                Spacer(Modifier.height(8.dp))
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
    onNavItemClick: ((String) -> Unit)? = null,
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
        onBack = onBack,
        onNavItemClick = onNavItemClick
    )
}

@Composable
private fun AdminStatCard(
    label: String,
    value: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = value.toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = OrderDesignTokens.CoastalTeal
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = OrderDesignTokens.TextSecondary
            )
        }
    }
}

@Composable
private fun AdminMenuCard(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(OrderDesignTokens.MintAccent.copy(alpha = 0.45f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = OrderDesignTokens.CoastalTealDark,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = OrderDesignTokens.TextPrimary
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = OrderDesignTokens.TextSecondary
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = OrderDesignTokens.TextMuted
            )
        }
    }
}
