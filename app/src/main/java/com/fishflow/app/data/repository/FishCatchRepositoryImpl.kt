package com.fishflow.app.data.repository

import com.fishflow.app.data.model.FishCatch
import com.fishflow.app.data.remote.FishCatchRemoteDataSource

class FishCatchRepositoryImpl(
    private val remoteDataSource: FishCatchRemoteDataSource = FishCatchRemoteDataSource()
) : FishCatchRepository {

    override suspend fun recordCatch(
        fishCatch: FishCatch
    ): Result<FishCatch> = runCatching {
        remoteDataSource.insertCatch(
            sellerId = fishCatch.sellerId,
            jenisIkan = fishCatch.namaIkan,
            beratKg = fishCatch.beratKg,
            tanggal = fishCatch.tanggalTangkap,
            fotoUrl = fishCatch.fotoUrl
        )
    }

    override suspend fun getCatchesBySeller(
        sellerId: String
    ): Result<List<FishCatch>> = runCatching {
        remoteDataSource.getCatchesBySeller(sellerId)
    }

    override suspend fun getCatchesByDate(
        sellerId: String,
        tanggal: String
    ): Result<List<FishCatch>> = runCatching {
        remoteDataSource.getCatchesByDate(sellerId, tanggal)
    }

    override suspend fun deleteCatch(
        catchId: String
    ): Result<Unit> = runCatching {
        remoteDataSource.deleteCatch(catchId)
        Unit
    }
}
