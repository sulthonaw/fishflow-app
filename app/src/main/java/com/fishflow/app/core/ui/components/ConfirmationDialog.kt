// ConfirmationDialog.kt
// Developer: Octavia
//
// Dialog konfirmasi umum (misal: "Yakin tolak pesanan?", "Hapus item dari keranjang?").
// Terima: title, message, onConfirm, onDismiss. Stateless.
package com.fishflow.app.core.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.fishflow.app.core.ui.theme.FishFlowTheme

@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title, fontWeight = FontWeight.Bold) },
        text = { Text(text = message) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Ya")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Preview
@Composable
private fun ConfirmationDialogPreview() {
    FishFlowTheme {
        ConfirmationDialog(
            title = "Hapus item?",
            message = "Hapus item dari keranjang?",
            onConfirm = {},
            onDismiss = {}
        )
    }
}
