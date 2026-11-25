package com.fishflow.app.data.repository

import com.fishflow.app.data.model.FishCatch

interface FishCatchRepository {

    suspend fun recordCatch(
        fishCatch: FishCatch
    ): Result<FishCatch>

    suspend fun getCatchesBySeller(
        sellerId: String
    ): Result<List<FishCatch>>

    suspend fun getCatchesByDate(
        sellerId: String,
        tanggal: String
    ): Result<List<FishCatch>>

    suspend fun deleteCatch(
        catchId: String
    ): Result<Unit>
}
