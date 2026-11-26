
package com.fishflow.app.feature.seller.`catch`

import android.widget.ImageView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCatchScreen(
    viewModel: AddCatchViewModel,
    onSaveSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.onFotoSelected(uri)
        }
    }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onSaveSuccess()
            viewModel.resetSavedState()
        }
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Tambah Tangkapan",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Catat hasil tangkapan ikan hari ini.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = uiState.jenisIkan,
                onValueChange = viewModel::onJenisIkanChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Jenis Ikan") },
                placeholder = { Text("Contoh: Tuna Sirip Kuning") },
                singleLine = true,
                enabled = !uiState.isLoading,
                isError = uiState.jenisIkanError != null,
                supportingText = {
                    uiState.jenisIkanError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )

            OutlinedTextField(
                value = uiState.berat,
                onValueChange = viewModel::onBeratChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Berat Tangkapan") },
                placeholder = { Text("Contoh: 10.5") },
                suffix = { Text("kg") },
                singleLine = true,
                enabled = !uiState.isLoading,
                isError = uiState.beratError != null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                supportingText = {
                    uiState.beratError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )

            OutlinedTextField(
                value = uiState.tanggalTangkap,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Tanggal Tangkap") },
                placeholder = { Text("Pilih tanggal") },
                readOnly = true,
                enabled = !uiState.isLoading,
                isError = uiState.tanggalError != null,
                supportingText = {
                    uiState.tanggalError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                trailingIcon = {
                    TextButton(
                        onClick = { showDatePicker = true },
                        enabled = !uiState.isLoading
                    ) {
                        Text("Pilih")
                    }
                }
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Foto Hasil Tangkapan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "Tambahkan foto ikan atau bukti timbang (opsional).",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedButton(
                    onClick = {
                        photoPickerLauncher.launch("image/*")
                    },
                    enabled = !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (uiState.fotoUri == null) {
                            "Pilih Foto dari Galeri"
                        } else {
                            "Ganti Foto"
                        }
                    )
                }

                uiState.fotoUri?.let { uri ->
                    AndroidView(
                        factory = { context ->
                            ImageView(context).apply {
                                scaleType = ImageView.ScaleType.CENTER_CROP
                            }
                        },
                        update = { imageView ->
                            imageView.setImageURI(uri)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                    )

                    TextButton(
                        onClick = {
                            viewModel.onFotoSelected(null)
                        },
                        enabled = !uiState.isLoading
                    ) {
                        Text("Hapus Foto")
                    }
                }
            }

            uiState.errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = viewModel::onSaveCatch,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(22.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text("Menyimpan...")
                } else {
                    Text(
                        text = "Simpan Tangkapan",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {
                Text("Kembali")
            }
        }
    }

    if (showDatePicker) {
        CatchDatePickerDialog(
            onDateSelected = { dateMillis ->
                val formattedDate = formatDate(dateMillis)
                viewModel.onTanggalChange(formattedDate)
                showDatePicker = false
            },
            onDismiss = {
                showDatePicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CatchDatePickerDialog(
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let(onDateSelected)
                },
                enabled = datePickerState.selectedDateMillis != null
            ) {
                Text("Pilih")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

private fun formatDate(dateMillis: Long): String {
    val formatter = SimpleDateFormat(
        "dd/MM/yyyy",
        Locale("id", "ID")
    ).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    return formatter.format(Date(dateMillis))
}
