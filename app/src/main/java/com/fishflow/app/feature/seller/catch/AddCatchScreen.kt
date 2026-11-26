package com.fishflow.app.feature.seller.`catch`

import android.widget.ImageView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fishflow.app.core.ui.components.CoastalHeroHeader
import com.fishflow.app.core.ui.components.ErrorToastEffect
import com.fishflow.app.core.ui.components.FishFlowTopBar
import com.fishflow.app.core.ui.components.OrderDesignTokens
import com.fishflow.app.core.util.DateFormatter
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCatchScreen(
    onSaveSuccess: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddCatchViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current

    ErrorToastEffect(
        errorMessage = uiState.errorMessage,
        onConsumed = viewModel::clearError
    )

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

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = OrderDesignTokens.CanvasBg,
        topBar = {
            FishFlowTopBar(
                title = "Catat Tangkapan",
                onBack = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            CoastalHeroHeader(
                badgeText = "Log Tangkapan Kapal",
                title = "Tambah Tangkapan",
                subtitle = "Catat hasil tangkapan ikan segar hari ini untuk stok jual."
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.jenisIkan,
                            onValueChange = viewModel::onJenisIkanChange,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Jenis Ikan") },
                            placeholder = { Text("Contoh: Tuna Sirip Kuning") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OrderDesignTokens.CoastalTeal,
                                unfocusedBorderColor = OrderDesignTokens.DividerColor
                            ),
                            enabled = !uiState.isLoading,
                            isError = uiState.jenisIkanError != null,
                            supportingText = {
                                uiState.jenisIkanError?.let {
                                    Text(text = it, color = OrderDesignTokens.ErrorText)
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
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OrderDesignTokens.CoastalTeal,
                                unfocusedBorderColor = OrderDesignTokens.DividerColor
                            ),
                            enabled = !uiState.isLoading,
                            isError = uiState.beratError != null,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            supportingText = {
                                uiState.beratError?.let {
                                    Text(text = it, color = OrderDesignTokens.ErrorText)
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
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OrderDesignTokens.CoastalTeal,
                                unfocusedBorderColor = OrderDesignTokens.DividerColor
                            ),
                            enabled = !uiState.isLoading,
                            isError = uiState.tanggalError != null,
                            supportingText = {
                                uiState.tanggalError?.let {
                                    Text(text = it, color = OrderDesignTokens.ErrorText)
                                }
                            },
                            trailingIcon = {
                                TextButton(
                                    onClick = { showDatePicker = true },
                                    enabled = !uiState.isLoading
                                ) {
                                    Text(
                                        text = "Pilih",
                                        color = OrderDesignTokens.CoastalTeal,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Foto Hasil Tangkapan",
                                color = OrderDesignTokens.TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Tambahkan foto ikan atau bukti timbang (opsional).",
                                color = OrderDesignTokens.TextSecondary,
                                fontSize = 12.sp
                            )

                            OutlinedButton(
                                onClick = { photoPickerLauncher.launch("image/*") },
                                enabled = !uiState.isLoading,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (uiState.fotoUri == null) {
                                        "Pilih Foto dari Galeri"
                                    } else {
                                        "Ganti Foto"
                                    },
                                    color = OrderDesignTokens.CoastalTeal,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            uiState.fotoUri?.let { uri ->
                                AndroidView(
                                    factory = { context ->
                                        ImageView(context).apply {
                                            scaleType = ImageView.ScaleType.CENTER_CROP
                                        }
                                    },
                                    update = { imageView -> imageView.setImageURI(uri) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                )
                                TextButton(
                                    onClick = { viewModel.onFotoSelected(null) },
                                    enabled = !uiState.isLoading
                                ) {
                                    Text("Hapus Foto", color = OrderDesignTokens.ErrorText)
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        val bytes = uiState.fotoUri?.let { uri ->
                            runCatching {
                                context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                            }.getOrNull()
                        }
                        viewModel.onSaveCatch(fotoBytes = bytes)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(9999.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OrderDesignTokens.CoastalTeal,
                        contentColor = Color.White
                    ),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Menyimpan...")
                    } else {
                        Text(
                            text = "Simpan Tangkapan",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

    if (showDatePicker) {
        CatchDatePickerDialog(
            onDateSelected = { dateMillis ->
                viewModel.onTanggalChange(DateFormatter.formatDate(Date(dateMillis)))
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
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
                onClick = { datePickerState.selectedDateMillis?.let(onDateSelected) },
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
