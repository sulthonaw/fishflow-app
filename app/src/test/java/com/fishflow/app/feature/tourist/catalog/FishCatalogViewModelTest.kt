package com.fishflow.app.feature.tourist.catalog

import com.fishflow.app.data.model.FishCategory
import com.fishflow.app.data.model.FishProduct
import com.fishflow.app.data.remote.InMemoryDestinationRemoteDataSource
import com.fishflow.app.data.repository.DestinationRepositoryImpl
import com.fishflow.app.data.repository.ProductRepository
import com.fishflow.app.data.repository.ProductRepositoryImpl
import com.fishflow.app.domain.usecase.product.GetProductsByRegionUseCase
import com.fishflow.app.domain.usecase.tourism.GetDestinationsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
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
class FishCatalogViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        InMemoryDestinationRemoteDataSource.resetToSeed()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `katalog awal memuat semua produk tersedia dan pilihan wilayah`() = runTest(dispatcher) {
        val viewModel = createViewModel()

        viewModel.load()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(5, state.products.size)
        assertTrue(state.products.all { it.isAvailable })
        assertEquals(listOf("Jolosutro", "Serang", "Tambakrejo"), state.regions)
    }

    @Test
    fun `filter wilayah dan kategori digabung`() = runTest(dispatcher) {
        val viewModel = createViewModel()
        viewModel.load()
        advanceUntilIdle()

        viewModel.onRegionFilter("Serang")
        viewModel.onCategoryFilter(FishCategory.UDANG_LOBSTER)
        advanceUntilIdle()

        assertEquals(
            listOf("prod-lobster-04"),
            viewModel.uiState.value.products.map { it.id }
        )
    }

    @Test
    fun `wilayah tanpa produk tersedia menghasilkan keadaan kosong`() = runTest(dispatcher) {
        val viewModel = createViewModel()
        viewModel.load()
        advanceUntilIdle()

        viewModel.onRegionFilter("Jolosutro")
        assertFalse(viewModel.uiState.value.isEmpty)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isEmpty)
    }

    @Test
    fun `hapus filter mengembalikan semua produk`() = runTest(dispatcher) {
        val viewModel = createViewModel()
        viewModel.load()
        viewModel.onRegionFilter("Jolosutro")
        viewModel.onCategoryFilter(FishCategory.KARANG)
        viewModel.onSearchChange("kakap")
        advanceUntilIdle()

        viewModel.onClearFilter()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.hasActiveFilter)
        assertEquals(5, state.products.size)
    }

    @Test
    fun `memuat ulang mempertahankan filter`() = runTest(dispatcher) {
        val viewModel = createViewModel()
        viewModel.load()
        viewModel.onRegionFilter("Tambakrejo")
        viewModel.onCategoryFilter(FishCategory.KARANG)
        advanceUntilIdle()

        viewModel.load()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Tambakrejo", state.selectedRegion)
        assertEquals(FishCategory.KARANG, state.selectedCategory)
        assertEquals(listOf("prod-kakap-02"), state.products.map { it.id })
    }

    @Test
    fun `gagal memuat menampilkan error lalu pulih setelah coba lagi`() = runTest(dispatcher) {
        val repository = FakeProductRepository(failuresLeft = 1)
        val viewModel = createViewModel(repository)

        viewModel.load()
        advanceUntilIdle()
        assertEquals("Koneksi terputus", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isEmpty)

        viewModel.onRetry()
        advanceUntilIdle()
        assertNull(viewModel.uiState.value.errorMessage)
        assertEquals(5, viewModel.uiState.value.products.size)
    }

    @Test
    fun `hasil wilayah lama tidak menimpa pilihan terbaru`() = runTest(dispatcher) {
        val repository = FakeProductRepository(
            delayByRegion = mapOf("Serang" to 1_000L, "Tambakrejo" to 10L)
        )
        val viewModel = createViewModel(repository)

        viewModel.onRegionFilter("Serang")
        advanceTimeBy(100)
        viewModel.onRegionFilter("Tambakrejo")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNull(state.errorMessage)
        assertEquals(
            listOf("prod-kakap-02", "prod-tuna-01"),
            state.products.map { it.id }.sorted()
        )
    }

    private fun createViewModel(
        repository: ProductRepository = ProductRepositoryImpl(ioDispatcher = dispatcher)
    ) = FishCatalogViewModel(
        getProductsByRegion = GetProductsByRegionUseCase(repository),
        getDestinations = GetDestinationsUseCase(DestinationRepositoryImpl.default())
    )

    private inner class FakeProductRepository(
        private var failuresLeft: Int = 0,
        private val delayByRegion: Map<String, Long> = emptyMap()
    ) : ProductRepository {
        private val real = ProductRepositoryImpl(ioDispatcher = dispatcher)

        override suspend fun getAll(): Result<List<FishProduct>> =
            if (failuresLeft-- > 0) {
                Result.failure(IllegalStateException("Koneksi terputus"))
            } else {
                real.getAll()
            }

        override suspend fun getByRegion(wilayah: String): Result<List<FishProduct>> {
            delay(delayByRegion[wilayah] ?: 0L)
            return real.getByRegion(wilayah)
        }

        override suspend fun getById(id: String): Result<FishProduct> = real.getById(id)
    }
}
