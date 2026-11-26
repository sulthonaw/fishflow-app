
package com.fishflow.app.feature.seller.`catch`

import android.net.Uri

data class AddCatchUiState(
    val jenisIkan: String = "",
    val berat: String = "",
    val tanggalTangkap: String = "",
    val fotoUri: Uri? = null,

    val jenisIkanError: String? = null,
    val beratError: String? = null,
    val tanggalError: String? = null,

    val isLoading: Boolean = false,
    val isSaved: Boolean = false,

    val errorMessage: String? = null
)
