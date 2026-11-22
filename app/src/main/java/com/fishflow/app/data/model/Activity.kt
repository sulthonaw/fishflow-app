package com.fishflow.app.data.model

// Activity.kt
// Developer: Fatih
//
// Data class untuk aktivitas wisata di suatu destinasi.
// Field: id, destinationId, nama, biaya (Double, nullable jika belum tersedia),
//        kategori (String, misal: "Retribusi", "Sewa Perahu", "Kuliner"), isAvailable (Boolean)
// Mapping ke tabel "activities" di Supabase.
// Biaya bisa null kalau belum diketahui - nanti ditandai di estimasi anggaran.
data class Activity(
    val id: String,
    val destinationId: String,
    val nama: String,
    val kategori: String,
    val biaya: Double? = null,
    val isAvailable: Boolean = biaya != null
) {
    // Biaya hanya boleh masuk perhitungan kalau datanya tersedia dan nilainya valid.
    val hasKnownCost: Boolean
        get() = isAvailable && biaya != null && biaya >= 0.0
}
