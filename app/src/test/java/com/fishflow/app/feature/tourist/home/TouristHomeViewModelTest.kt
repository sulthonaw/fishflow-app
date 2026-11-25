package com.fishflow.app.feature.tourist.home

import com.fishflow.app.data.model.FishProduct
import com.fishflow.app.data.remote.InMemoryDestinationRemoteDataSource
import com.fishflow.app.data.repository.DestinationRepositoryImpl
import com.fishflow.app.data.repository.ProductRepository
import com.fishflow.app.data.repository.ProductRepositoryImpl
import com.fishflow.app.domain.usecase.product.GetProductsByRegionUseCase
import com.fishflow.app.domain.usecase.tourism.GetDestinationsUseCase
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
class TouristHomeViewModelTest {

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
    fun `beranda memuat empat produk tersedia pertama`() = runTest(dispatcher) {
        val viewModel = createViewModel()

        viewModel.onRefresh()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(4, state.todayCatches.size)
        assertTrue(state.todayCatches.all { it.isAvailable })
    }

    @Test
    fun `tidak ada produk tersedia menghasilkan keadaan kosong`() = runTest(dispatcher) {
        val viewModel = createViewModel(FakeProductRepository(Result.success(emptyList())))

        viewModel.onRefresh()
        assertFalse(viewModel.uiState.value.isEmpty)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isEmpty)
    }

    @Test
    fun `gagal memuat menampilkan error lalu pulih setelah coba lagi`() = runTest(dispatcher) {
        val repository = FakeProductRepository(
            Result.failure(IllegalStateException("Koneksi terputus"))
        )
        val viewModel = createViewModel(repository)

        viewModel.onRefresh()
        advanceUntilIdle()
        assertEquals("Koneksi terputus", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isEmpty)

        repository.result = null
        viewModel.onRefresh()
        advanceUntilIdle()
        assertNull(viewModel.uiState.value.errorMessage)
        assertEquals(4, viewModel.uiState.value.todayCatches.size)
    }

    @Test
    fun `kata kunci tersimpan dan tidak mengubah daftar beranda`() = runTest(dispatcher) {
        val viewModel = createViewModel()
        viewModel.onRefresh()
        advanceUntilIdle()

        viewModel.onSearchChange("tuna")

        assertEquals("tuna", viewModel.uiState.value.searchQuery)
        assertEquals(4, viewModel.uiState.value.todayCatches.size)
    }

    @Test
    fun `chip kategori mengganti section dan bertahan saat dimuat ulang`() = runTest(dispatcher) {
        val viewModel = createViewModel()
        viewModel.onRefresh()
        advanceUntilIdle()
        assertEquals(TouristHomeUiState.CATEGORY_FISH, viewModel.uiState.value.selectedCategory)

        viewModel.onCategoryClick(TouristHomeUiState.CATEGORY_TOURISM)
        viewModel.onRefresh()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(TouristHomeUiState.CATEGORY_TOURISM, state.selectedCategory)
        assertFalse(state.isDestinationLoading)
        assertEquals(3, state.featuredDestinations.size)
    }

    @Test
    fun `gagal memuat ikan tidak menutupi destinasi`() = runTest(dispatcher) {
        val viewModel = createViewModel(
            FakeProductRepository(Result.failure(IllegalStateException("Koneksi terputus")))
        )

        viewModel.onRefresh()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Koneksi terputus", state.errorMessage)
        assertNull(state.destinationErrorMessage)
        assertEquals(3, state.featuredDestinations.size)
    }

    private fun createViewModel(
        repository: ProductRepository = ProductRepositoryImpl(ioDispatcher = dispatcher)
    ) = TouristHomeViewModel(
        getProducts = GetProductsByRegionUseCase(repository),
        getDestinations = GetDestinationsUseCase(DestinationRepositoryImpl.default())
    )

    // result null berarti memakai data contoh.
    private inner class FakeProductRepository(
        var result: Result<List<FishProduct>>?
    ) : ProductRepository {
        private val real = ProductRepositoryImpl(ioDispatcher = dispatcher)

        override suspend fun getAll(): Result<List<FishProduct>> = result ?: real.getAll()

        override suspend fun getByRegion(wilayah: String): Result<List<FishProduct>> =
            real.getByRegion(wilayah)

        override suspend fun getById(id: String): Result<FishProduct> = real.getById(id)
    }
}
