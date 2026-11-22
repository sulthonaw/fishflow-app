package com.fishflow.app.data.model

data class FishCatch(
    val id: String = "",
    val sellerId: String = "",
    val jenisIkan: String = "",
    val beratKg: Double = 0.0,
    val tanggalTangkap: String = "",
    val fotoUrl: String? = null,
)
