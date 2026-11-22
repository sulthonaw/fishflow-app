package com.fishflow.app.data.model

// Destination.kt
// Developer: Fatih
//
// Data class untuk destinasi wisata pesisir.
// Field: id, nama, wilayah, deskripsi, fotoUrl, latitude (Double), longitude (Double)
// Mapping ke tabel "destinations" di Supabase.
data class Destination(
    val id: String,
    val nama: String,
    val wilayah: String,
    val deskripsi: String,
    val fotoUrl: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)
