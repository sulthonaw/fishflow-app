package com.fishflow.app.domain.usecase.tourism

import com.fishflow.app.data.model.Destination
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchDestinationsUseCaseTest {

    private val searchDestinations = SearchDestinationsUseCase()

    private val destinations = listOf(
        destination("serang", nama = "Pantai Serang", wilayah = "Serang"),
        destination("tambakrejo", nama = "Pantai Tambakrejo", wilayah = "Tambakrejo")
    )

    @Test
    fun `cocok dengan nama atau wilayah tanpa membedakan huruf besar`() {
        assertEquals(listOf("serang"), search("pantai SERANG"))
        assertEquals(listOf("tambakrejo"), search(" tambak "))
        assertEquals(listOf("serang", "tambakrejo"), search("pantai"))
    }

    @Test
    fun `kata kunci kosong atau tidak cocok tidak menghasilkan destinasi`() {
        assertTrue(search("").isEmpty())
        assertTrue(search("   ").isEmpty())
        assertTrue(search("tuna").isEmpty())
    }

    private fun search(query: String): List<String> =
        searchDestinations(destinations, query).map { it.id }

    private fun destination(id: String, nama: String, wilayah: String) = Destination(
        id = id,
        nama = nama,
        wilayah = wilayah,
        deskripsi = ""
    )
}
