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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fishflow.app.core.ui.components.CoastalHeroHeader
import com.fishflow.app.core.ui.components.FishFlowTopBar
import com.fishflow.app.core.ui.components.OrderDesignTokens

@Composable
fun ActivityFormScreen(
    form: ActivityFormState,
    onFormChange: (ActivityFormState) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pageTitle = if (form.isEditing) "Edit Aktivitas" else "Tambah Aktivitas"

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
                badgeText = if (form.isEditing) "Perbarui Aktivitas" else "Aktivitas Baru",
                title = pageTitle,
                subtitle = "Atur nama aktivitas wisata beserta komponen biayanya."
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
                            label = { Text("Nama aktivitas") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OrderDesignTokens.CoastalTeal,
                                unfocusedBorderColor = OrderDesignTokens.DividerColor
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = form.kategori,
                            onValueChange = { onFormChange(form.copy(kategori = it)) },
                            label = { Text("Kategori") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OrderDesignTokens.CoastalTeal,
                                unfocusedBorderColor = OrderDesignTokens.DividerColor
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(OrderDesignTokens.SurfaceMuted)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Biaya sudah tersedia",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = OrderDesignTokens.TextPrimary,
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
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = OrderDesignTokens.CoastalTeal,
                                    unfocusedBorderColor = OrderDesignTokens.DividerColor
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Text(
                                text = "Aktivitas ini akan ditandai \"Belum tersedia\" dan tidak dihitung Rp0.",
                                fontSize = 12.sp,
                                color = OrderDesignTokens.TextSecondary
                            )
                        }

                        form.errorMessage?.let { message ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(OrderDesignTokens.ErrorBg)
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = message,
                                    color = OrderDesignTokens.ErrorText,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
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
                        text = "Simpan Aktivitas",
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
