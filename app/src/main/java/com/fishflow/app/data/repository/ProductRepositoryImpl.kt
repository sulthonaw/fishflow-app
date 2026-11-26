// ProductRepositoryImpl.kt
// Developer: Octavia & Fakhri
//
// Implementasi ProductRepository.
// - Data produk diambil dari ProductRemoteDataSource
// - Angka stok diambil dari ProductStockSource (perhitungan Fungsi 1)
// - Akses data dijalankan di Dispatchers.IO, error dibungkus Result
package com.fishflow.app.data.repository

import com.fishflow.app.data.model.FishProduct
import com.fishflow.app.data.remote.InMemoryProductRemoteDataSource
import com.fishflow.app.data.remote.ProductRemoteDataSource
import com.fishflow.app.data.remote.SupabaseProductRemoteDataSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ProductRepositoryImpl(
    private val remote: ProductRemoteDataSource = InMemoryProductRemoteDataSource,
    private val stockSource: ProductStockSource? = InMemoryProductStockSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ProductRepository {

    override suspend fun getAll(): Result<List<FishProduct>> = request {
        remote.getAllProducts().withAvailableStock()
    }

    override suspend fun getByRegion(wilayah: String): Result<List<FishProduct>> = request {
        val products = if (wilayah.isBlank()) {
            remote.getAllProducts()
        } else {
            remote.getProductsByRegion(wilayah.trim())
        }
        products.withAvailableStock()
    }

    override suspend fun getById(id: String): Result<FishProduct> = request {
        require(id.isNotBlank()) { "ID produk tidak valid" }
        val product = remote.getProductById(id) ?: error("Produk tidak ditemukan")
        listOf(product).withAvailableStock().first()
    }

    override suspend fun getActiveProducts(): List<FishProduct> = withContext(ioDispatcher) {
        remote.getActiveProducts().withAvailableStock()
    }

    override suspend fun getProductsByRegion(wilayah: String): List<FishProduct> = withContext(ioDispatcher) {
        val products = if (wilayah.isBlank()) {
            remote.getAllProducts()
        } else {
            remote.getProductsByRegion(wilayah.trim())
        }
        products.withAvailableStock()
    }

    override suspend fun getProductsBySeller(sellerId: String): List<FishProduct> = withContext(ioDispatcher) {
        remote.getProductsBySeller(sellerId).withAvailableStock()
    }

    override suspend fun getProductById(id: String): FishProduct? = withContext(ioDispatcher) {
        if (id.isBlank()) return@withContext null
        val product = remote.getProductById(id) ?: return@withContext null
        listOf(product).withAvailableStock().firstOrNull()
    }

    override suspend fun updatePrice(productId: String, hargaBaru: Double) = withContext(ioDispatcher) {
        remote.updatePrice(productId, hargaBaru)
    }

    override suspend fun updateStock(productId: String, stokBaru: Double) = withContext(ioDispatcher) {
        remote.updateStock(productId, stokBaru)
    }

    override suspend fun updateStatus(productId: String, isActive: Boolean) = withContext(ioDispatcher) {
        remote.updateStatus(productId, isActive)
    }

    override fun observeProducts(): Flow<List<FishProduct>> {
        return remote.subscribeToProductChanges()
    }

    // Produk yang tidak punya data stok dianggap 0 sehingga tidak tampil di katalog.
    private suspend fun List<FishProduct>.withAvailableStock(): List<FishProduct> {
        val source = stockSource ?: return this
        val stock = source.getAvailableStock().getOrThrow()
        return map { product -> product.copy(stokTersedia = stock[product.id] ?: 0.0) }
    }

    // CancellationException diteruskan supaya pembatalan coroutine tidak dianggap error data.
    private suspend fun <T> request(block: suspend () -> T): Result<T> = withContext(ioDispatcher) {
        try {
            Result.success(block())
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (error: Exception) {
            Result.failure(error)
        }
    }

    companion object {
        fun default(): ProductRepository = ProductRepositoryImpl()

        fun supabase(): ProductRepository = ProductRepositoryImpl(
            remote = SupabaseProductRemoteDataSource(),
            stockSource = null
        )
    }
}
