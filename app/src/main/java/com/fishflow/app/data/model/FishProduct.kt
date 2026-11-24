// FishProduct.kt
// Developer: Octavia
//
// Koneksi: Octavia memegang fitur katalog produk ikan (Fungsi 4). Model FishProduct
// adalah entitas utama yang dikonsumsi langsung oleh katalog wisatawan, filter wilayah,
// dan detail produk ikan miliknya.
//
// Data class untuk produk ikan yang dijual. Mapping ke tabel "products" di Supabase.
//
// PENTING: stokTersedia adalah HASIL perhitungan stok Fungsi 1
// (tangkapan - terjual - teralokasi) yang diisi ProductRepository dari ProductStockSource.
// Jangan dikurangi alokasi lagi di sini supaya stok tidak terpotong dua kali.
package com.fishflow.app.data.model

import androidx.annotation.DrawableRes

data class FishProduct(
    val id: String,
    val sellerId: String,
    val namaIkan: String,
    val kategori: FishCategory,
    val hargaPerKg: Double,
    val wilayah: String,
    val lokasiPengambilan: String,
    val namaPenjual: String,
    val namaKapal: String? = null,
    val status: ProductStatus = ProductStatus.AKTIF,
    val stokTersedia: Double = 0.0,
    val batasPengambilan: String? = null,
    val infoPenanganan: String? = null,
    @param:DrawableRes val imageRes: Int? = null
) {
    // Syarat tampil di katalog: produk aktif dan masih ada stok yang bisa dipesan.
    val isAvailable: Boolean
        get() = status == ProductStatus.AKTIF && stokTersedia > 0.0
}
