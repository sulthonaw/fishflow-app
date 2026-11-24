// ProductRepository.kt
// Developer: Octavia & Fakhri
//
// Repository interface mendefinisikan kontrak data produk untuk katalog wisata dan inventory penjual.
// Semua produk yang dikembalikan untuk katalog sudah memuat stokTersedia dari ProductStockSource.
package com.fishflow.app.data.repository

import com.fishflow.app.data.model.FishProduct
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

interface ProductRepository {

    suspend fun getAll(): Result<List<FishProduct>>

    suspend fun getByRegion(wilayah: String): Result<List<FishProduct>>

    suspend fun getById(id: String): Result<FishProduct>

    // Mengambil seluruh produk yang aktif dan memiliki stok
    suspend fun getActiveProducts(): List<FishProduct> = emptyList()

    // Mengambil produk berdasarkan wilayah
    suspend fun getProductsByRegion(
        wilayah: String
    ): List<FishProduct> = emptyList()

    // Mengambil produk berdasarkan ID penjual
    suspend fun getProductsBySeller(
        sellerId: String
    ): List<FishProduct> = emptyList()

    // Mengambil detail satu produk berdasarkan ID
    suspend fun getProductById(
        id: String
    ): FishProduct? = null

    // Memperbarui harga produk
    suspend fun updatePrice(
        productId: String,
        hargaBaru: Double
    ) {}

    // Memperbarui stok produk
    suspend fun updateStock(
        productId: String,
        stokBaru: Double
    ) {}

    // Mengubah status aktif/nonaktif produk
    suspend fun updateStatus(
        productId: String,
        isActive: Boolean
    ) {}

    // Mengamati perubahan data produk
    fun observeProducts(): Flow<List<FishProduct>> = emptyFlow()
}
