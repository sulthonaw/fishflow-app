package com.fishflow.app.feature.seller.dashboard

import com.fishflow.app.data.model.FishProduct

data class SellerDashboardUiState(
    val namaUser: String = "Penjual",

    val stokAktifKg: Double = 0.0,

    val pesananBaru: Int = 0,

    val omzetTotal: Double = 0.0,

    val inventoryPreview: List<FishProduct> = emptyList(),

    val isLoading: Boolean = false,

    val errorMessage: String? = null
)
