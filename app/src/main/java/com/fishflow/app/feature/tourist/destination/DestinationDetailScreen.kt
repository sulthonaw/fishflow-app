package com.fishflow.app.feature.tourist.destination

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NearMe
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fishflow.app.core.ui.components.CoastalHeroHeader
import com.fishflow.app.core.ui.components.FishFlowTopBar
import com.fishflow.app.core.ui.components.OrderDesignTokens
import com.fishflow.app.data.model.Activity
import com.fishflow.app.data.model.Destination
import com.fishflow.app.feature.tourist.destination.components.ActivityChecklistItem

@Composable
fun DestinationDetailScreen(
    uiState: DestinationDetailUiState,
    onRetry: () -> Unit,
    onPlanBudget: (Destination, Set<String>) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    initialSelectedIds: Set<String> = emptySet()
) {
    val context = LocalContext.current
    val destination = uiState.destination
    var selectedIds by remember(destination?.id) { mutableStateOf(initialSelectedIds) }

    com.fishflow.app.core.ui.components.ErrorToastEffect(errorMessage = uiState.errorMessage)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = OrderDesignTokens.CanvasBg,
        topBar = {
            FishFlowTopBar(
                title = "Detail Destinasi",
                onBack = onBack
            )
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = OrderDesignTokens.CoastalTeal)
                }
            }

            destination == null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = uiState.errorMessage ?: "Destinasi tidak ditemukan",
                        color = OrderDesignTokens.ErrorText,
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = onRetry,
                        shape = RoundedCornerShape(9999.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrderDesignTokens.CoastalTeal)
                    ) {
                        Text("Coba lagi")
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                ) {
                    CoastalHeroHeader(
                        badgeText = destination.wilayah,
                        title = destination.nama,
                        subtitle = "Pilih aktivitas wisata yang ingin dimasukkan ke estimasi anggaran."
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            shadowElevation = 2.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "Tentang Destinasi",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OrderDesignTokens.TextPrimary
                                )
                                Text(
                                    text = destination.deskripsi,
                                    fontSize = 13.sp,
                                    lineHeight = 19.sp,
                                    color = OrderDesignTokens.TextSecondary
                                )
                                if (destination.latitude != null && destination.longitude != null) {
                                    OutlinedButton(
                                        onClick = {
                                            openGoogleMaps(context, destination.latitude, destination.longitude)
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.NearMe,
                                            contentDescription = null,
                                            tint = OrderDesignTokens.CoastalTeal,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(Modifier.width(6.dp))
                                        Text(
                                            text = "Buka di Google Maps",
                                            color = OrderDesignTokens.CoastalTeal,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            shadowElevation = 2.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Aktivitas & Biaya",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OrderDesignTokens.TextPrimary
                                )
                                Text(
                                    text = "Centang aktivitas yang ingin dimasukkan ke estimasi anggaran.",
                                    fontSize = 12.sp,
                                    color = OrderDesignTokens.TextSecondary
                                )
                                Spacer(Modifier.height(4.dp))
                                if (uiState.activities.isEmpty()) {
                                    Text(
                                        text = "Belum ada aktivitas yang terdaftar.",
                                        fontSize = 13.sp,
                                        color = OrderDesignTokens.TextMuted
                                    )
                                } else {
                                    uiState.activities.forEachIndexed { index, activity ->
                                        ActivitySelectionRow(
                                            activity = activity,
                                            checked = activity.id in selectedIds,
                                            onCheckedChange = { checked ->
                                                selectedIds = if (checked) {
                                                    selectedIds + activity.id
                                                } else {
                                                    selectedIds - activity.id
                                                }
                                            }
                                        )
                                        if (index != uiState.activities.lastIndex) {
                                            HorizontalDivider(color = OrderDesignTokens.DividerColor)
                                        }
                                    }
                                }
                            }
                        }

                        if (uiState.errorMessage != null) {
                            Text(
                                text = uiState.errorMessage,
                                color = OrderDesignTokens.ErrorText,
                                fontSize = 12.sp
                            )
                        }

                        Button(
                            onClick = { onPlanBudget(destination, selectedIds) },
                            shape = RoundedCornerShape(9999.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = OrderDesignTokens.CoastalTeal,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(
                                text = "Susun Rencana & Estimasi",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
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
fun DestinationDetailScreen(
    destinationId: String,
    onPlanBudget: (Destination, Set<String>) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    initialSelectedIds: Set<String> = emptySet(),
    viewModel: DestinationDetailViewModel = viewModel(
        key = destinationId,
        factory = DestinationDetailViewModel.factory(destinationId)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    DestinationDetailScreen(
        uiState = uiState,
        onRetry = viewModel::onRetry,
        onPlanBudget = onPlanBudget,
        onBack = onBack,
        modifier = modifier,
        initialSelectedIds = initialSelectedIds
    )
}

@Composable
private fun ActivitySelectionRow(
    activity: Activity,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ActivityChecklistItem(
            activity = activity,
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.weight(1f)
        )
    }
}

private fun openGoogleMaps(context: Context, latitude: Double, longitude: Double) {
    val uri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$latitude,$longitude")
    val intent = Intent(Intent.ACTION_VIEW, uri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    runCatching { context.startActivity(intent) }
}
