// ProductRepositoryImpl.kt
// Developer: Octavia
//
// Implementasi ProductRepository.
// - Data produk diambil dari ProductRemoteDataSource
// - Angka stok diambil dari ProductStockSource (perhitungan Fungsi 1)
// - Akses data dijalankan di Dispatchers.IO, error dibungkus Result
//
// Kalau stok gagal dibaca, seluruh permintaan dianggap gagal supaya katalog
// tidak pernah menampilkan angka stok yang salah.
package com.fishflow.app.data.repository

import com.fishflow.app.data.model.FishProduct
import com.fishflow.app.data.remote.InMemoryProductRemoteDataSource
import com.fishflow.app.data.remote.ProductRemoteDataSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductRepositoryImpl(
    private val remote: ProductRemoteDataSource = InMemoryProductRemoteDataSource,
    private val stockSource: ProductStockSource = InMemoryProductStockSource,
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

    // Produk yang tidak punya data stok dianggap 0 sehingga tidak tampil di katalog.
    private suspend fun List<FishProduct>.withAvailableStock(): List<FishProduct> {
        val stock = stockSource.getAvailableStock().getOrThrow()
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
    }
}
