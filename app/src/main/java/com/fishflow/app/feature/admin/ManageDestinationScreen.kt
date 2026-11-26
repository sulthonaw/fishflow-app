package com.fishflow.app.feature.admin

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fishflow.app.core.ui.components.AdminBottomBarItems
import com.fishflow.app.core.ui.components.CoastalHeroHeader
import com.fishflow.app.core.ui.components.FishFlowBottomBar
import com.fishflow.app.core.ui.components.FishFlowTopBar
import com.fishflow.app.core.ui.components.OrderDesignTokens
import com.fishflow.app.data.model.Activity
import com.fishflow.app.data.model.Destination
import com.fishflow.app.feature.tourist.budget.formatRupiah

// ManageDestinationScreen.kt
// Developer: Fatih
//
// Koneksi: Fatih bikin DestinationListScreen (read) untuk wisatawan,
// sekarang bikin versi admin-nya (CRUD). Model Destination dan Activity sama.
//
// CRUD destinasi wisata dan aktivitasnya.
// UI: Daftar destinasi + tombol tambah. Klik item -> form edit.
// Form: nama, wilayah, deskripsi, foto, aktivitas + biayanya.
// Tombol hapus dengan konfirmasi.
@Composable
fun ManageDestinationScreen(
    uiState: ManageDestinationUiState,
    onAddDestination: () -> Unit,
    onEditDestination: (Destination) -> Unit,
    onDeleteDestination: (Destination) -> Unit,
    onConfirmDeleteDestination: () -> Unit,
    onDismissDeleteDestination: () -> Unit,
    onToggleExpanded: (String) -> Unit,
    onAddActivity: (String) -> Unit,
    onEditActivity: (Activity) -> Unit,
    onDeleteActivity: (Activity) -> Unit,
    onConfirmDeleteActivity: () -> Unit,
    onDismissDeleteActivity: () -> Unit,
    onDestinationFormChange: (DestinationFormState) -> Unit,
    onSaveDestination: () -> Unit,
    onDismissDestinationForm: () -> Unit,
    onActivityFormChange: (ActivityFormState) -> Unit,
    onSaveActivity: () -> Unit,
    onDismissActivityForm: () -> Unit,
    onDismissMessage: () -> Unit,
    onRefresh: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = OrderDesignTokens.CanvasBg,
        topBar = {
            FishFlowTopBar(
                title = "Kelola Destinasi",
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
                badgeText = "Konten Wisata",
                title = "Kelola Destinasi",
                subtitle = "Tambah destinasi, atur aktivitas, dan perbarui komponen biaya."
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onAddDestination,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OrderDesignTokens.CoastalTeal,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Tambah Destinasi")
                    }
                    TextButton(onClick = onRefresh) {
                        Text("Muat ulang", color = OrderDesignTokens.CoastalTeal)
                    }
                }

                uiState.successMessage?.let { message ->
                    MessageRow(
                        message = message,
                        isError = false,
                        onDismiss = onDismissMessage
                    )
                }
                uiState.errorMessage?.let { message ->
                    MessageRow(
                        message = message,
                        isError = true,
                        onDismiss = onDismissMessage
                    )
                }

                Spacer(Modifier.height(8.dp))

                when {
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = OrderDesignTokens.CoastalTeal)
                        }
                    }

                    uiState.destinations.isEmpty() -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Belum ada destinasi",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Tambahkan destinasi pertama lewat tombol di atas.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(uiState.destinations, key = { it.id }) { destination ->
                                DestinationAdminCard(
                                    destination = destination,
                                    activities = uiState.activitiesFor(destination.id),
                                    expanded = destination.id in uiState.expandedDestinationIds,
                                    onToggleExpanded = { onToggleExpanded(destination.id) },
                                    onEdit = { onEditDestination(destination) },
                                    onDelete = { onDeleteDestination(destination) },
                                    onAddActivity = { onAddActivity(destination.id) },
                                    onEditActivity = onEditActivity,
                                    onDeleteActivity = onDeleteActivity
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    uiState.destinationForm?.let { form ->
        DestinationFormDialog(
            form = form,
            onFormChange = onDestinationFormChange,
            onSave = onSaveDestination,
            onDismiss = onDismissDestinationForm
        )
    }

    uiState.activityForm?.let { form ->
        ActivityFormDialog(
            form = form,
            onFormChange = onActivityFormChange,
            onSave = onSaveActivity,
            onDismiss = onDismissActivityForm
        )
    }

    uiState.destinationPendingDelete?.let { destination ->
        AlertDialog(
            onDismissRequest = onDismissDeleteDestination,
            title = { Text("Hapus destinasi?") },
            text = {
                Text(
                    "Destinasi \"${destination.nama}\" beserta aktivitasnya akan dihapus. " +
                        "Tindakan ini tidak bisa dibatalkan."
                )
            },
            confirmButton = {
                TextButton(onClick = onConfirmDeleteDestination) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissDeleteDestination) {
                    Text("Batal")
                }
            }
        )
    }

    uiState.activityPendingDelete?.let { activity ->
        AlertDialog(
            onDismissRequest = onDismissDeleteActivity,
            title = { Text("Hapus aktivitas?") },
            text = { Text("Aktivitas \"${activity.nama}\" akan dihapus dari destinasi ini.") },
            confirmButton = {
                TextButton(onClick = onConfirmDeleteActivity) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissDeleteActivity) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun ManageDestinationScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ManageDestinationViewModel = viewModel(
        factory = ManageDestinationViewModel.factory()
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ManageDestinationScreen(
        uiState = uiState,
        onAddDestination = viewModel::onAddDestinationClick,
        onEditDestination = viewModel::onEditDestinationClick,
        onDeleteDestination = viewModel::onDeleteDestinationClick,
        onConfirmDeleteDestination = viewModel::onConfirmDeleteDestination,
        onDismissDeleteDestination = viewModel::onDismissDeleteDestination,
        onToggleExpanded = viewModel::onToggleExpanded,
        onAddActivity = viewModel::onAddActivityClick,
        onEditActivity = viewModel::onEditActivityClick,
        onDeleteActivity = viewModel::onDeleteActivityClick,
        onConfirmDeleteActivity = viewModel::onConfirmDeleteActivity,
        onDismissDeleteActivity = viewModel::onDismissDeleteActivity,
        onDestinationFormChange = viewModel::onDestinationFormChange,
        onSaveDestination = viewModel::onSaveDestination,
        onDismissDestinationForm = viewModel::onDismissDestinationForm,
        onActivityFormChange = viewModel::onActivityFormChange,
        onSaveActivity = viewModel::onSaveActivity,
        onDismissActivityForm = viewModel::onDismissActivityForm,
        onDismissMessage = viewModel::onMessageShown,
        onRefresh = viewModel::load,
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
private fun DestinationAdminCard(
    destination: Destination,
    activities: List<Activity>,
    expanded: Boolean,
    onToggleExpanded: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onAddActivity: () -> Unit,
    onEditActivity: (Activity) -> Unit,
    onDeleteActivity: (Activity) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = destination.nama,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = destination.wilayah,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = destination.deskripsi,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onToggleExpanded) {
                    Text(
                        if (expanded) {
                            "Sembunyikan aktivitas"
                        } else {
                            "Aktivitas (${activities.size})"
                        }
                    )
                }
                Spacer(Modifier.weight(1f))
                TextButton(onClick = onEdit) {
                    Text("Edit")
                }
                TextButton(onClick = onDelete) {
                    Text("Hapus")
                }
            }

            if (expanded) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(Modifier.height(8.dp))

                if (activities.isEmpty()) {
                    Text(
                        text = "Belum ada aktivitas untuk destinasi ini.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    activities.forEachIndexed { index, activity ->
                        ActivityAdminRow(
                            activity = activity,
                            onEdit = { onEditActivity(activity) },
                            onDelete = { onDeleteActivity(activity) }
                        )
                        if (index != activities.lastIndex) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        }
                    }
                }

                TextButton(onClick = onAddActivity) {
                    Text("Tambah Aktivitas")
                }
            }
        }
    }
}

@Composable
private fun ActivityAdminRow(
    activity: Activity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = activity.nama,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = "${activity.kategori} - ${
                    if (activity.hasKnownCost) {
                        formatRupiah(activity.biaya ?: 0.0)
                    } else {
                        "Belum tersedia"
                    }
                }",
                style = MaterialTheme.typography.bodySmall,
                color = if (activity.hasKnownCost) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
        }
        TextButton(onClick = onEdit) {
            Text("Edit")
        }
        TextButton(onClick = onDelete) {
            Text("Hapus")
        }
    }
}

@Composable
private fun DestinationFormDialog(
    form: DestinationFormState,
    onFormChange: (DestinationFormState) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (form.isEditing) "Edit Destinasi" else "Tambah Destinasi")
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = form.nama,
                    onValueChange = { onFormChange(form.copy(nama = it)) },
                    label = { Text("Nama destinasi") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = form.wilayah,
                    onValueChange = { onFormChange(form.copy(wilayah = it)) },
                    label = { Text("Wilayah") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = form.deskripsi,
                    onValueChange = { onFormChange(form.copy(deskripsi = it)) },
                    label = { Text("Deskripsi") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = form.fotoUrl,
                    onValueChange = { onFormChange(form.copy(fotoUrl = it)) },
                    label = { Text("URL foto (opsional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = form.latitude,
                        onValueChange = { onFormChange(form.copy(latitude = it)) },
                        label = { Text("Latitude") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = form.longitude,
                        onValueChange = { onFormChange(form.copy(longitude = it)) },
                        label = { Text("Longitude") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                form.errorMessage?.let { message ->
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onSave) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Composable
private fun ActivityFormDialog(
    form: ActivityFormState,
    onFormChange: (ActivityFormState) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (form.isEditing) "Edit Aktivitas" else "Tambah Aktivitas")
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = form.nama,
                    onValueChange = { onFormChange(form.copy(nama = it)) },
                    label = { Text("Nama aktivitas") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = form.kategori,
                    onValueChange = { onFormChange(form.copy(kategori = it)) },
                    label = { Text("Kategori") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Biaya sudah tersedia",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = form.isAvailable,
                        onCheckedChange = { onFormChange(form.copy(isAvailable = it)) }
                    )
                }
                if (form.isAvailable) {
                    OutlinedTextField(
                        value = form.biaya,
                        onValueChange = { onFormChange(form.copy(biaya = it)) },
                        label = { Text("Biaya (Rp)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(
                        text = "Aktivitas ini akan ditandai \"Belum tersedia\" dan tidak dihitung Rp0.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                form.errorMessage?.let { message ->
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onSave) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Composable
private fun MessageRow(
    message: String,
    isError: Boolean,
    onDismiss: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = if (isError) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.primary
            },
            modifier = Modifier.weight(1f)
        )
        TextButton(onClick = onDismiss) {
            Text("Tutup")
        }
    }
}
