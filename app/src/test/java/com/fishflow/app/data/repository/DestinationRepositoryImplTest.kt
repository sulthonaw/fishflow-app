package com.fishflow.app.data.repository

import com.fishflow.app.data.model.Activity
import com.fishflow.app.data.model.Destination
import com.fishflow.app.data.remote.InMemoryDestinationRemoteDataSource
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DestinationRepositoryImplTest {

    private val repository = DestinationRepositoryImpl(InMemoryDestinationRemoteDataSource)

    @Before
    fun setUp() {
        InMemoryDestinationRemoteDataSource.resetToSeed()
    }

    @Test
    fun `getAll mengembalikan destinasi seed`() = runTest {
        val result = repository.getAll()

        assertTrue(result.isSuccess)
        assertEquals(3, result.getOrThrow().size)
    }

    @Test
    fun `getByRegion memfilter wilayah tanpa membedakan huruf besar kecil`() = runTest {
        val result = repository.getByRegion("tambakrejo")

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrThrow().size)
        assertEquals("Pantai Tambakrejo", result.getOrThrow().first().nama)
    }

    @Test
    fun `getById gagal untuk destinasi yang tidak ada`() = runTest {
        val result = repository.getById("dest-tidak-ada")

        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
    }

    @Test
    fun `createDestination menormalisasi input dan memberi id`() = runTest {
        val result = repository.createDestination(
            Destination(
                id = "",
                nama = "  Pantai Baru  ",
                wilayah = "  Wilayah Baru ",
                deskripsi = "  Deskripsi baru  "
            )
        )

        assertTrue(result.isSuccess)
        val created = result.getOrThrow()
        assertTrue(created.id.isNotBlank())
        assertEquals("Pantai Baru", created.nama)
        assertEquals("Wilayah Baru", created.wilayah)
    }

    @Test
    fun `createDestination menolak nama kosong`() = runTest {
        val result = repository.createDestination(
            Destination("", "   ", "Tambakrejo", "Deskripsi")
        )

        assertTrue(result.isFailure)
    }

    @Test
    fun `createActivity menolak destinasi yang tidak ada`() = runTest {
        val result = repository.createActivity(
            Activity(
                id = "",
                destinationId = "dest-tidak-ada",
                nama = "Pemandu",
                kategori = "Pemandu",
                biaya = 75_000.0
            )
        )

        assertTrue(result.isFailure)
    }

    @Test
    fun `createActivity menambah aktivitas pada destinasi yang ada`() = runTest {
        val before = repository.getActivities("dest-serang").getOrThrow().size

        val result = repository.createActivity(
            Activity(
                id = "",
                destinationId = "dest-serang",
                nama = "Pemandu Lokal",
                kategori = "Pemandu",
                biaya = 75_000.0
            )
        )

        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().id.isNotBlank())
        assertEquals(before + 1, repository.getActivities("dest-serang").getOrThrow().size)
    }

    @Test
    fun `updateActivity menyimpan perubahan`() = runTest {
        val activity = repository.getActivities("dest-tambakrejo").getOrThrow()
            .first { it.id == "act-tambakrejo-pemandu" }

        val result = repository.updateActivity(
            activity.copy(nama = "Pemandu Wisata", biaya = 60_000.0, isAvailable = true)
        )

        assertTrue(result.isSuccess)
        val updated = repository.getActivities("dest-tambakrejo").getOrThrow()
            .first { it.id == "act-tambakrejo-pemandu" }
        assertEquals("Pemandu Wisata", updated.nama)
        assertEquals(60_000.0, updated.biaya ?: 0.0, 0.001)
    }

    @Test
    fun `deleteDestination ikut menghapus aktivitasnya`() = runTest {
        val result = repository.deleteDestination("dest-tambakrejo")
        assertTrue(result.isSuccess)

        assertTrue(repository.getById("dest-tambakrejo").isFailure)
        assertTrue(repository.getActivities("dest-tambakrejo").getOrThrow().isEmpty())
    }

    @Test
    fun `deleteActivity gagal jika aktivitas tidak ada`() = runTest {
        val result = repository.deleteActivity("act-tidak-ada")

        assertTrue(result.isFailure)
    }

    @Test
    fun `aktivitas seed dengan biaya null tetap terbaca`() = runTest {
        val activities = repository.getActivities("dest-tambakrejo").getOrThrow()
        val pemandu = activities.first { it.id == "act-tambakrejo-pemandu" }

        assertFalse(pemandu.isAvailable)
        assertEquals(null, pemandu.biaya)
    }
}
