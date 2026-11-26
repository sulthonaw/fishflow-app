package com.fishflow.app.core.ui.components

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

/**
 * Menampilkan pesan error dalam bentuk Toast setiap kali [errorMessage] tidak null,
 * lalu memanggil [onConsumed] agar pesan error tidak muncul berulang saat recomposition.
 */
@Composable
fun ErrorToastEffect(
    errorMessage: String?,
    onConsumed: () -> Unit = {}
) {
    val context = LocalContext.current
    LaunchedEffect(errorMessage) {
        val message = errorMessage?.trim().orEmpty()
        if (message.isNotEmpty()) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            onConsumed()
        }
    }
}
