package com.fishflow.app.feature.tourist.catalog

import com.fishflow.app.data.repository.ProductRepositoryImpl
import com.fishflow.app.data.repository.ProductStockSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FishDetailViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private var stok = 2.5

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `jumlah tidak melewati stok dan tidak kurang dari satu`() = runTest(dispatcher) {
        val viewModel = createViewModel("prod-tuna-01")
        viewModel.load()
        advanceUntilIdle()

        viewModel.onDecreaseQuantity()
        assertEquals(1, viewModel.uiState.value.quantity)

        repeat(5) { viewModel.onIncreaseQuantity() }
        assertEquals(2, viewModel.uiState.value.quantity)
        assertTrue(viewModel.uiState.value.canOrder)
    }

    @Test
    fun `jumlah menyesuaikan saat stok berkurang`() = runTest(dispatcher) {
        val viewModel = createViewModel("prod-tuna-01")
        viewModel.load()
        advanceUntilIdle()
        viewModel.onIncreaseQuantity()

        stok = 1.0
        viewModel.load()
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.quantity)
    }

    @Test
    fun `stok habis tidak bisa dipesan`() = runTest(dispatcher) {
        stok = 0.0
        val viewModel = createViewModel("prod-tuna-01")
        viewModel.load()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.canOrder)
    }

    @Test
    fun `produk nonaktif tidak bisa dipesan`() = runTest(dispatcher) {
        val viewModel = createViewModel("prod-layur-07")
        viewModel.load()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.canOrder)
    }

    @Test
    fun `produk tidak ditemukan menampilkan error`() = runTest(dispatcher) {
        val viewModel = createViewModel("tidak-ada")
        viewModel.load()
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.product)
        assertEquals("Produk tidak ditemukan", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `data produk diteruskan ke item keranjang`() = runTest(dispatcher) {
        val viewModel = createViewModel("prod-tuna-01")
        viewModel.load()
        advanceUntilIdle()

        val item = viewModel.uiState.value.product!!.toCartItem(quantity = 2)

        assertEquals("prod-tuna-01", item.productId)
        assertEquals(2, item.qty)
        assertEquals(2, item.availableStockKg)
        assertEquals(90_000.0, item.subtotal, 0.0)
    }

    private fun createViewModel(productId: String) = FishDetailViewModel(
        productId = productId,
        repository = ProductRepositoryImpl(
            stockSource = object : ProductStockSource {
                override suspend fun getAvailableStock() =
                    Result.success(mapOf("prod-tuna-01" to stok, "prod-layur-07" to 10.0))
            },
            ioDispatcher = dispatcher
        )
    )
}
