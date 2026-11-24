package com.fishflow.app.data.repository

import com.fishflow.app.data.remote.InMemoryProductRemoteDataSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class ProductRepositoryImplTest {

    @Test
    fun `stok produk sama persis dengan sumber stok`() = runTest {
        val repository = repositoryWithStock(mapOf("prod-tuna-01" to 7.25))

        val product = repository.getById("prod-tuna-01").getOrThrow()

        assertEquals(7.25, product.stokTersedia, 0.0)
    }

    @Test
    fun `produk tanpa data stok dianggap nol`() = runTest {
        val repository = repositoryWithStock(emptyMap())

        val products = repository.getAll().getOrThrow()

        assertTrue(products.isNotEmpty())
        assertTrue(products.all { it.stokTersedia == 0.0 })
    }

    @Test
    fun `getByRegion hanya mengambil produk di wilayah itu`() = runTest {
        val products = ProductRepositoryImpl.default().getByRegion(" serang ").getOrThrow()

        assertTrue(products.isNotEmpty())
        assertTrue(products.all { it.wilayah == "Serang" })
    }

    @Test
    fun `gagal membaca stok membuat permintaan gagal`() = runTest {
        val repository = repository { Result.failure(IllegalStateException("Stok gagal dimuat")) }

        val result = repository.getAll()

        assertEquals("Stok gagal dimuat", result.exceptionOrNull()?.message)
    }

    @Test
    fun `id tidak dikenal menghasilkan error`() = runTest {
        val result = ProductRepositoryImpl.default().getById("tidak-ada")

        assertEquals("Produk tidak ditemukan", result.exceptionOrNull()?.message)
    }

    @Test
    fun `pembatalan coroutine tidak dibungkus menjadi error data`() = runTest {
        val repository = repository { throw CancellationException("dibatalkan") }

        try {
            repository.getAll()
            fail("Seharusnya melempar CancellationException")
        } catch (cancellation: CancellationException) {
            assertEquals("dibatalkan", cancellation.message)
        }
    }

    private fun repositoryWithStock(stock: Map<String, Double>) =
        repository { Result.success(stock) }

    private fun repository(stock: () -> Result<Map<String, Double>>) = ProductRepositoryImpl(
        remote = InMemoryProductRemoteDataSource,
        stockSource = object : ProductStockSource {
            override suspend fun getAvailableStock() = stock()
        }
    )
}
