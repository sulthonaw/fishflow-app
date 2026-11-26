package com.fishflow.app.feature.admin

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fishflow.app.core.ui.components.CoastalHeroHeader
import com.fishflow.app.core.ui.components.ErrorToastEffect
import com.fishflow.app.core.ui.components.FishFlowTopBar
import com.fishflow.app.core.ui.components.OrderDesignTokens

@Composable
fun DestinationFormScreen(
    form: DestinationFormState,
    onFormChange: (DestinationFormState) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pageTitle = if (form.isEditing) "Edit Destinasi" else "Tambah Destinasi"

    ErrorToastEffect(
        errorMessage = form.errorMessage,
        onConsumed = { onFormChange(form.copy(errorMessage = null)) }
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = OrderDesignTokens.CanvasBg,
        topBar = {
            FishFlowTopBar(
                title = pageTitle,
                onBack = onCancel
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
                badgeText = if (form.isEditing) "Perbarui Data" else "Destinasi Baru",
                title = pageTitle,
                subtitle = "Lengkapi informasi destinasi wisata pesisir di bawah ini."
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
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = form.nama,
                            onValueChange = { onFormChange(form.copy(nama = it)) },
                            label = { Text("Nama destinasi") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OrderDesignTokens.CoastalTeal,
                                unfocusedBorderColor = OrderDesignTokens.DividerColor
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = form.wilayah,
                            onValueChange = { onFormChange(form.copy(wilayah = it)) },
                            label = { Text("Wilayah") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OrderDesignTokens.CoastalTeal,
                                unfocusedBorderColor = OrderDesignTokens.DividerColor
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = form.deskripsi,
                            onValueChange = { onFormChange(form.copy(deskripsi = it)) },
                            label = { Text("Deskripsi") },
                            minLines = 3,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OrderDesignTokens.CoastalTeal,
                                unfocusedBorderColor = OrderDesignTokens.DividerColor
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = form.fotoUrl,
                            onValueChange = { onFormChange(form.copy(fotoUrl = it)) },
                            label = { Text("URL foto (opsional)") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OrderDesignTokens.CoastalTeal,
                                unfocusedBorderColor = OrderDesignTokens.DividerColor
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = form.latitude,
                                onValueChange = { onFormChange(form.copy(latitude = it)) },
                                label = { Text("Latitude") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = OrderDesignTokens.CoastalTeal,
                                    unfocusedBorderColor = OrderDesignTokens.DividerColor
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = form.longitude,
                                onValueChange = { onFormChange(form.copy(longitude = it)) },
                                label = { Text("Longitude") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = OrderDesignTokens.CoastalTeal,
                                    unfocusedBorderColor = OrderDesignTokens.DividerColor
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Button(
                    onClick = onSave,
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
                        text = "Simpan Destinasi",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                TextButton(
                    onClick = onCancel,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Batal",
                        color = OrderDesignTokens.TextSecondary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}
