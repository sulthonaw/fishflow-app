
package com.fishflow.app.data.repository

import com.fishflow.app.data.model.FishProduct
import kotlinx.coroutines.flow.Flow

interface ProductRepository {

    // Mengambil seluruh produk yang aktif dan memiliki stok
    suspend fun getActiveProducts(): List<FishProduct>

    // Mengambil produk berdasarkan wilayah
    suspend fun getProductsByRegion(
        wilayah: String
    ): List<FishProduct>

    // Mengambil produk berdasarkan ID penjual
    suspend fun getProductsBySeller(
        sellerId: String
    ): List<FishProduct>

    // Mengambil detail satu produk berdasarkan ID
    suspend fun getProductById(
        id: String
    ): FishProduct?

    // Memperbarui harga produk
    suspend fun updatePrice(
        productId: String,
        hargaBaru: Double
    )

    // Memperbarui stok produk
    suspend fun updateStock(
        productId: String,
        stokBaru: Double
    )

    // Mengubah status aktif/nonaktif produk
    suspend fun updateStatus(
        productId: String,
        isActive: Boolean
    )

    // Mengamati perubahan data produk
    fun observeProducts(): Flow<List<FishProduct>>
}
