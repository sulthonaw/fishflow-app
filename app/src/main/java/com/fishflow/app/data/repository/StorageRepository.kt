package com.fishflow.app.data.repository

import com.fishflow.app.data.remote.StorageRemoteDataSource
import com.fishflow.app.supabase.SupabaseConfig

interface StorageRepository {
    suspend fun uploadCatchPhoto(
        sellerId: String,
        imageBytes: ByteArray,
        fileName: String = "catch.jpg"
    ): Result<String>

    suspend fun uploadProductImage(
        productId: String,
        imageBytes: ByteArray
    ): Result<String>

    fun getPhotoUrl(path: String): String
}

class StorageRepositoryImpl(
    private val remoteDataSource: StorageRemoteDataSource = StorageRemoteDataSource()
) : StorageRepository {

    override suspend fun uploadCatchPhoto(
        sellerId: String,
        imageBytes: ByteArray,
        fileName: String
    ): Result<String> = runCatching {
        remoteDataSource.uploadCatchPhoto(
            sellerId = sellerId,
            imageBytes = imageBytes,
            fileName = fileName
        )
    }

    override suspend fun uploadProductImage(
        productId: String,
        imageBytes: ByteArray
    ): Result<String> = runCatching {
        remoteDataSource.uploadProductImage(
            productId = productId,
            imageBytes = imageBytes
        )
    }

    override fun getPhotoUrl(path: String): String =
        remoteDataSource.getPublicUrl(
            bucket = SupabaseConfig.STORAGE_BUCKET,
            path = path
        )
}
