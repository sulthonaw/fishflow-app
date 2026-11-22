package com.fishflow.app.domain.usecase.tourism

import com.fishflow.app.data.model.Activity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculateBudgetEstimateUseCaseTest {

    private val useCase = CalculateBudgetEstimateUseCase()

    @Test
    fun `total hanya menjumlahkan biaya yang diketahui`() {
        val estimate = useCase(
            listOf(
                activity("a", biaya = 5_000.0),
                activity("b", biaya = 45_000.0)
            )
        )

        assertEquals(50_000.0, estimate.totalBiaya, 0.001)
        assertTrue(estimate.catatanBelumTersedia.isEmpty())
        assertEquals(2, estimate.selectedActivities.size)
    }

    @Test
    fun `biaya null ditandai belum tersedia dan tidak dihitung nol`() {
        val estimate = useCase(
            listOf(
                activity("a", biaya = 5_000.0),
                activity("b", nama = "Sewa Perahu", biaya = null, isAvailable = false)
            )
        )

        assertEquals(5_000.0, estimate.totalBiaya, 0.001)
        assertEquals(listOf("Sewa Perahu"), estimate.catatanBelumTersedia)
        assertTrue(estimate.hasUnavailableCost)
    }

    @Test
    fun `biaya terisi tetapi isAvailable false tetap diabaikan`() {
        val estimate = useCase(
            listOf(activity("a", nama = "Pemandu", biaya = 100_000.0, isAvailable = false))
        )

        assertEquals(0.0, estimate.totalBiaya, 0.001)
        assertEquals(listOf("Pemandu"), estimate.catatanBelumTersedia)
    }

    @Test
    fun `biaya nol tetap dianggap diketahui`() {
        val estimate = useCase(
            listOf(activity("a", nama = "Retribusi", biaya = 0.0))
        )

        assertEquals(0.0, estimate.totalBiaya, 0.001)
        assertTrue(estimate.catatanBelumTersedia.isEmpty())
    }

    @Test
    fun `biaya negatif masuk catatan dan tidak mengurangi total`() {
        val estimate = useCase(
            listOf(
                activity("a", biaya = 10_000.0),
                activity("b", nama = "Biaya Salah", biaya = -5_000.0)
            )
        )

        assertEquals(10_000.0, estimate.totalBiaya, 0.001)
        assertEquals(listOf("Biaya Salah"), estimate.catatanBelumTersedia)
    }

    @Test
    fun `aktivitas duplikat hanya dihitung sekali`() {
        val activity = activity("a", biaya = 7_500.0)
        val estimate = useCase(listOf(activity, activity))

        assertEquals(7_500.0, estimate.totalBiaya, 0.001)
        assertEquals(1, estimate.selectedActivities.size)
    }

    @Test
    fun `daftar kosong menghasilkan total nol`() {
        val estimate = useCase(emptyList())

        assertEquals(0.0, estimate.totalBiaya, 0.001)
        assertTrue(estimate.selectedActivities.isEmpty())
        assertTrue(estimate.catatanBelumTersedia.isEmpty())
    }

    private fun activity(
        id: String,
        nama: String = "Aktivitas $id",
        biaya: Double? = null,
        isAvailable: Boolean = biaya != null
    ) = Activity(
        id = id,
        destinationId = "dest",
        nama = nama,
        kategori = "Umum",
        biaya = biaya,
        isAvailable = isAvailable
    )
}
