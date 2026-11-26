
package com.fishflow.app.data.remote

import java.util.UUID

data class MockFishCatch(
    val id: String,
    val sellerId: String,
    val jenisIkan: String,
    val beratKg: Double,
    val tanggal: String,
    val fotoUrl: String? = null
)

class FishCatchRemoteDataSource {
    private val catches = mutableListOf<MockFishCatch>()

    suspend fun insertCatch(
        sellerId: String,
        jenisIkan: String,
        beratKg: Double,
        tanggal: String,
        fotoUrl: String? = null
    ): MockFishCatch {

        require(sellerId.isNotBlank()) {
            "Seller ID tidak boleh kosong."
        }

        require(jenisIkan.isNotBlank()) {
            "Jenis ikan harus diisi."
        }

        require(beratKg > 0) {
            "Berat tangkapan harus lebih dari 0 kg."
        }

        val newCatch = MockFishCatch(
            id = UUID.randomUUID().toString(),
            sellerId = sellerId,
            jenisIkan = jenisIkan,
            beratKg = beratKg,
            tanggal = tanggal,
            fotoUrl = fotoUrl
        )

        catches.add(newCatch)

        return newCatch
    }

    suspend fun getCatchesBySeller(
        sellerId: String
    ): List<MockFishCatch> {

        return catches
            .filter { it.sellerId == sellerId }
            .sortedByDescending { it.tanggal }
    }

    suspend fun getCatchesByDate(
        sellerId: String,
        tanggal: String
    ): List<MockFishCatch> {

        return catches.filter {
            it.sellerId == sellerId &&
            it.tanggal == tanggal
        }
    }

    suspend fun deleteCatch(
        catchId: String
    ): Boolean {

        val target = catches.find {
            it.id == catchId
        }

        return if (target != null) {
            catches.remove(target)
        } else {
            false
        }
    }

    suspend fun getAllCatches(): List<MockFishCatch> {
        return catches.toList()
    }

    suspend fun clearAllCatches() {
        catches.clear()
    }
}
