package com.fishflow.app.data.model

data class SellerProfile(
    val userId: String,
    val namaKapal: String? = null,
    val stokAktifKg: Double = 0.0,
    val pesananBaru: Int = 0,
    val omzet: Double = 0.0
)
