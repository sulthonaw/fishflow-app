package com.fishflow.app.feature.tourist.destination

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fishflow.app.data.model.Activity
import com.fishflow.app.data.model.Destination
import com.fishflow.app.feature.tourist.destination.components.ActivityChecklistItem

// DestinationDetailScreen.kt
// Developer: Fatih
//
// Detail satu destinasi wisata.
//
// UI:
// - Foto besar destinasi
// - Nama, wilayah, deskripsi lengkap
// - Lokasi (latitude/longitude - bisa tampilkan link Google Maps)
// - Daftar aktivitas yang tersedia + biayanya masing-masing
// - Tombol "Susun Rencana & Estimasi" -> navigasi ke BudgetPlannerScreen
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        TextButton(onClick = onBack) {
            Text("Kembali")
        }

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            destination == null -> {
                Text(
                    text = uiState.errorMessage ?: "Destinasi tidak ditemukan",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(8.dp))
                Button(onClick = onRetry) {
                    Text("Coba lagi")
                }
            }

            else -> {
                DestinationHero(destination.nama)

                Spacer(Modifier.height(16.dp))

                Text(
                    text = destination.nama,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(4.dp))

                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = destination.wilayah,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = destination.deskripsi,
                    style = MaterialTheme.typography.bodyMedium
                )

                if (destination.latitude != null && destination.longitude != null) {
                    Spacer(Modifier.height(8.dp))
                    TextButton(
                        onClick = {
                            openGoogleMaps(context, destination.latitude, destination.longitude)
                        }
                    ) {
                        Text("Buka di Google Maps")
                    }
                }

                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Aktivitas & Biaya",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Centang aktivitas yang ingin dimasukkan ke estimasi anggaran.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(8.dp))

                if (uiState.activities.isEmpty()) {
                    Text(
                        text = "Belum ada aktivitas yang terdaftar.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        }
                    }
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
                    onClick = { onPlanBudget(destination, selectedIds) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Susun Rencana & Estimasi")
                }

                Spacer(Modifier.height(8.dp))
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
private fun DestinationHero(nama: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.secondaryContainer
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = nama.take(1).uppercase(),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = nama,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
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
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
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
    val intent = Intent(Intent.ACTION_VIEW, uri)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    runCatching { context.startActivity(intent) }
}
