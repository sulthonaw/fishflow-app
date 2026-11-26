
package com.fishflow.app.feature.seller.`catch`.components

import android.widget.ImageView
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CatchFormFields(
    jenisIkan: String,
    berat: String,
    tanggalTangkap: String,
    fotoUri: Uri?,
    jenisIkanError: String?,
    beratError: String?,
    tanggalError: String?,
    isEnabled: Boolean,
    onJenisIkanChange: (String) -> Unit,
    onBeratChange: (String) -> Unit,
    onTanggalClick: () -> Unit,
    onFotoClick: () -> Unit,
    onRemoveFotoClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        OutlinedTextField(
            value = jenisIkan,
            onValueChange = onJenisIkanChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Jenis Ikan") },
            placeholder = { Text("Contoh: Tuna Sirip Kuning") },
            singleLine = true,
            enabled = isEnabled,
            isError = jenisIkanError != null,
            supportingText = {
                jenisIkanError?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        OutlinedTextField(
            value = berat,
            onValueChange = onBeratChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Berat Tangkapan") },
            placeholder = { Text("Contoh: 10.5") },
            suffix = { Text("kg") },
            singleLine = true,
            enabled = isEnabled,
            isError = beratError != null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
            supportingText = {
                beratError?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        OutlinedTextField(
            value = tanggalTangkap,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Tanggal Tangkap") },
            placeholder = { Text("Pilih tanggal") },
            readOnly = true,
            enabled = isEnabled,
            isError = tanggalError != null,
            supportingText = {
                tanggalError?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            trailingIcon = {
                TextButton(
                    onClick = onTanggalClick,
                    enabled = isEnabled
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
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Tambahkan foto ikan atau bukti timbang (opsional).",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedButton(
                onClick = onFotoClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = isEnabled
            ) {
                Text(
                    text = if (fotoUri == null) {
                        "Pilih Foto dari Galeri"
                    } else {
                        "Ganti Foto"
                    }
                )
            }

            fotoUri?.let { uri ->
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
                    onClick = onRemoveFotoClick,
                    enabled = isEnabled
                ) {
                    Text("Hapus Foto")
                }
            }
        }
    }
}
