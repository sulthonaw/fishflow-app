package com.fishflow.app.data.repository

import com.fishflow.app.data.model.FishCatch
import kotlinx.coroutines.delay

class FishCatchRepositoryImpl : FishCatchRepository {

    private val catches = mutableListOf<FishCatch>()

    override suspend fun recordCatch(
        fishCatch: FishCatch
    ): Result<FishCatch> {
        return try {
            delay(300)

            catches.add(fishCatch)

            Result.success(fishCatch)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    override suspend fun getCatchesBySeller(
        sellerId: String
    ): Result<List<FishCatch>> {
        return try {
            delay(300)

            val sellerCatches = catches.filter {
                it.sellerId == sellerId
            }

            Result.success(sellerCatches)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    override suspend fun getCatchesByDate(
        sellerId: String,
        tanggal: String
    ): Result<List<FishCatch>> {
        return try {
            delay(300)

            val filteredCatches = catches.filter {
                it.sellerId == sellerId &&
                it.tanggalTangkap == tanggal
            }

            Result.success(filteredCatches)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    override suspend fun deleteCatch(
        catchId: String
    ): Result<Unit> {
        return try {
            delay(300)

            val removed = catches.removeAll {
                it.id == catchId
            }

            if (removed) {
                Result.success(Unit)
            } else {
                Result.failure(
                    Exception("Data tangkapan tidak ditemukan.")
                )
            }
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}
