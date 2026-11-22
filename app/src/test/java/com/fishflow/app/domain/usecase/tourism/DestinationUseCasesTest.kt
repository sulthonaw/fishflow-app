package com.fishflow.app.domain.usecase.tourism

import com.fishflow.app.data.model.Activity
import com.fishflow.app.data.model.Destination
import com.fishflow.app.data.repository.DestinationRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DestinationUseCasesTest {

    @Test
    fun `GetDestinationsUseCase memakai getAll saat wilayah kosong`() = runTest {
        val repository = FakeDestinationRepository()
        val useCase = GetDestinationsUseCase(repository)

        val result = useCase()

        assertTrue(result.isSuccess)
        assertEquals(1, repository.getAllCalls)
        assertEquals(0, repository.getByRegionCalls)
    }

    @Test
    fun `GetDestinationsUseCase memakai getByRegion saat wilayah diisi`() = runTest {
        val repository = FakeDestinationRepository()
        val useCase = GetDestinationsUseCase(repository)

        val result = useCase("Tambakrejo")

        assertTrue(result.isSuccess)
        assertEquals("Tambakrejo", repository.lastRegion)
        assertEquals(1, repository.getByRegionCalls)
        assertEquals(0, repository.getAllCalls)
    }

    @Test
    fun `GetActivitiesByDestinationUseCase menolak id kosong`() = runTest {
        val repository = FakeDestinationRepository()
        val useCase = GetActivitiesByDestinationUseCase(repository)

        val result = useCase("  ")

        assertTrue(result.isFailure)
        assertEquals(0, repository.getActivitiesCalls)
    }

    @Test
    fun `GetActivitiesByDestinationUseCase mengembalikan semua aktivitas termasuk yang belum tersedia`() = runTest {
        val repository = FakeDestinationRepository(
            activities = listOf(
                activity("a", biaya = 5_000.0),
                activity("b", biaya = null, isAvailable = false)
            )
        )
        val useCase = GetActivitiesByDestinationUseCase(repository)

        val result = useCase("dest-1")

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrThrow().size)
        assertEquals(1, repository.getActivitiesCalls)
    }

    private fun activity(
        id: String,
        biaya: Double?,
        isAvailable: Boolean = biaya != null
    ) = Activity(
        id = id,
        destinationId = "dest-1",
        nama = "Aktivitas $id",
        kategori = "Umum",
        biaya = biaya,
        isAvailable = isAvailable
    )

    private class FakeDestinationRepository(
        private val destinations: List<Destination> = listOf(
            Destination("dest-1", "Pantai Tambakrejo", "Tambakrejo", "Deskripsi")
        ),
        private val activities: List<Activity> = emptyList()
    ) : DestinationRepository {

        var getAllCalls = 0
        var getByRegionCalls = 0
        var getActivitiesCalls = 0
        var lastRegion: String? = null

        override suspend fun getAll(): Result<List<Destination>> {
            getAllCalls++
            return Result.success(destinations)
        }

        override suspend fun getByRegion(wilayah: String): Result<List<Destination>> {
            getByRegionCalls++
            lastRegion = wilayah
            return Result.success(destinations.filter { it.wilayah == wilayah })
        }

        override suspend fun getById(id: String): Result<Destination> =
            Result.success(destinations.first { it.id == id })

        override suspend fun getActivities(destinationId: String): Result<List<Activity>> {
            getActivitiesCalls++
            return Result.success(activities.filter { it.destinationId == destinationId })
        }

        override suspend fun createDestination(destination: Destination): Result<Destination> =
            Result.success(destination)

        override suspend fun updateDestination(destination: Destination): Result<Destination> =
            Result.success(destination)

        override suspend fun deleteDestination(id: String): Result<Unit> = Result.success(Unit)

        override suspend fun createActivity(activity: Activity): Result<Activity> =
            Result.success(activity)

        override suspend fun updateActivity(activity: Activity): Result<Activity> =
            Result.success(activity)

        override suspend fun deleteActivity(id: String): Result<Unit> = Result.success(Unit)
    }
}
