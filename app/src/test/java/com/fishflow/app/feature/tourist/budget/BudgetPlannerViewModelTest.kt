package com.fishflow.app.feature.tourist.budget

import com.fishflow.app.data.remote.InMemoryDestinationRemoteDataSource
import com.fishflow.app.data.repository.DestinationRepositoryImpl
import com.fishflow.app.domain.usecase.tourism.CalculateBudgetEstimateUseCase
import com.fishflow.app.domain.usecase.tourism.GetActivitiesByDestinationUseCase
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BudgetPlannerViewModelTest {

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
    fun `memuat aktivitas destinasi`() = runTest(dispatcher) {
        val viewModel = createViewModel()

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("Pantai Tambakrejo", state.destinationName)
        assertEquals(5, state.allActivities.size)
        assertEquals(0.0, state.totalBiaya, 0.001)
    }

    @Test
    fun `toggle aktivitas menghitung total secara otomatis`() = runTest(dispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onActivityToggle("act-tambakrejo-retribusi")
        assertEquals(5_000.0, viewModel.uiState.value.totalBiaya, 0.001)

        viewModel.onActivityToggle("act-tambakrejo-parkir")
        assertEquals(8_000.0, viewModel.uiState.value.totalBiaya, 0.001)

        viewModel.onActivityToggle("act-tambakrejo-retribusi")
        assertEquals(3_000.0, viewModel.uiState.value.totalBiaya, 0.001)
    }

    @Test
    fun `aktivitas tanpa biaya masuk catatan tanpa menambah total`() = runTest(dispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onActivityToggle("act-tambakrejo-retribusi")
        viewModel.onActivityToggle("act-tambakrejo-pemandu")

        val state = viewModel.uiState.value
        assertEquals(5_000.0, state.totalBiaya, 0.001)
        assertTrue(state.hasUnavailableCost)
        assertTrue(state.catatanBelumTersedia.contains("Pemandu Lokal"))
    }

    @Test
    fun `pilihan awal dari detail destinasi langsung dihitung`() = runTest(dispatcher) {
        val viewModel = createViewModel(
            initialSelectedIds = setOf("act-tambakrejo-retribusi", "act-tambakrejo-parkir")
        )

        advanceUntilIdle()

        assertEquals(8_000.0, viewModel.uiState.value.totalBiaya, 0.001)
        assertEquals(2, viewModel.uiState.value.selectedCount)
    }

    @Test
    fun `reset mengosongkan pilihan dan total`() = runTest(dispatcher) {
        val viewModel = createViewModel(initialSelectedIds = setOf("act-tambakrejo-retribusi"))
        advanceUntilIdle()

        viewModel.onReset()

        val state = viewModel.uiState.value
        assertTrue(state.selectedActivityIds.isEmpty())
        assertEquals(0.0, state.totalBiaya, 0.001)
        assertFalse(state.hasUnavailableCost)
    }

    @Test
    fun `destinasi tidak valid menampilkan pesan error`() = runTest(dispatcher) {
        val repository = DestinationRepositoryImpl.default()
        val viewModel = BudgetPlannerViewModel(
            destinationId = " ",
            destinationName = "",
            getActivities = GetActivitiesByDestinationUseCase(repository),
            calculateBudgetEstimate = CalculateBudgetEstimateUseCase()
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.errorMessage)
        assertTrue(state.allActivities.isEmpty())
    }

    private fun createViewModel(
        initialSelectedIds: Set<String> = emptySet()
    ): BudgetPlannerViewModel {
        val repository = DestinationRepositoryImpl.default()
        return BudgetPlannerViewModel(
            destinationId = "dest-tambakrejo",
            destinationName = "Pantai Tambakrejo",
            getActivities = GetActivitiesByDestinationUseCase(repository),
            calculateBudgetEstimate = CalculateBudgetEstimateUseCase(),
            initialSelectedIds = initialSelectedIds
        )
    }
}
