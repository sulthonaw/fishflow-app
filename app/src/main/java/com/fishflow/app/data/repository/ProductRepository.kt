// ProductRepository.kt
// Developer: Octavia
//
// Koneksi: Octavia bertanggung jawab penuh pada domain produk perikanan (Fungsi 4).
// Repository interface ini mendefinisikan kontrak data produk untuk katalog wisata.
//
// Semua produk yang dikembalikan sudah memuat stokTersedia dari ProductStockSource.
package com.fishflow.app.data.repository

import com.fishflow.app.data.model.FishProduct

interface ProductRepository {

    suspend fun getAll(): Result<List<FishProduct>>

    suspend fun getByRegion(wilayah: String): Result<List<FishProduct>>

    suspend fun getById(id: String): Result<FishProduct>
}
