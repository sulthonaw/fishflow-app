package com.fishflow.app.data.remote

import com.fishflow.app.core.network.SupabaseClientProvider
import com.fishflow.app.supabase.SupabaseConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import java.util.UUID

class StorageRemoteDataSource(
    private val supabase: SupabaseClient = SupabaseClientProvider.client
) {

    companion object {
        private const val DEFAULT_BUCKET = SupabaseConfig.STORAGE_BUCKET
    }

    /**
     * Upload foto hasil tangkapan nelayan ke bucket fishflow-media.
     */
    suspend fun uploadCatchPhoto(
        sellerId: String,
        imageBytes: ByteArray,
        fileName: String
    ): String {
        require(sellerId.isNotBlank()) {
            "Seller ID tidak boleh kosong"
        }

        require(imageBytes.isNotEmpty()) {
            "File foto tidak boleh kosong"
        }

        val safeSellerId = sanitizeSegment(sellerId)
        val safeFileName = sanitizeFileName(fileName)
        val filePath = "catches/$safeSellerId/${UUID.randomUUID()}_$safeFileName"

        val bucket = supabase.storage.from(DEFAULT_BUCKET)
        bucket.upload(path = filePath, data = imageBytes) {
            upsert = false
        }

        return bucket.publicUrl(filePath)
    }

    /**
     * Upload gambar produk ikan ke bucket fishflow-media.
     */
    suspend fun uploadProductImage(
        productId: String,
        imageBytes: ByteArray
    ): String {
        require(productId.isNotBlank()) {
            "Product ID tidak boleh kosong"
        }

        require(imageBytes.isNotEmpty()) {
            "File gambar tidak boleh kosong"
        }

        val safeProductId = sanitizeSegment(productId)
        val filePath = "products/$safeProductId/${UUID.randomUUID()}.jpg"

        val bucket = supabase.storage.from(DEFAULT_BUCKET)
        bucket.upload(path = filePath, data = imageBytes) {
            upsert = false
        }

        return bucket.publicUrl(filePath)
    }

    /**
     * Mengambil URL publik sebuah gambar.
     */
    fun getPublicUrl(
        bucket: String = DEFAULT_BUCKET,
        path: String
    ): String {
        require(bucket.isNotBlank()) {
            "Bucket tidak boleh kosong"
        }

        require(path.isNotBlank()) {
            "Path foto tidak boleh kosong"
        }

        return supabase.storage
            .from(bucket)
            .publicUrl(path)
    }

    /**
     * Menghapus foto dari Supabase Storage.
     */
    suspend fun deletePhoto(
        bucket: String = DEFAULT_BUCKET,
        path: String
    ) {
        require(bucket.isNotBlank()) {
            "Bucket tidak boleh kosong"
        }

        require(path.isNotBlank()) {
            "Path foto tidak boleh kosong"
        }

        supabase.storage
            .from(bucket)
            .delete(path)
    }

    private fun sanitizeSegment(value: String): String {
        return value.trim()
            .replace(Regex("[^A-Za-z0-9_-]"), "")
            .also {
                require(it.isNotBlank()) {
                    "Path identifier tidak valid"
                }
            }
    }

    private fun sanitizeFileName(value: String): String {
        val name = value.substringAfterLast("/")
            .substringAfterLast("\\")

        return name.replace(
            Regex("[^A-Za-z0-9._-]"),
            "_"
        ).ifBlank { "catch_photo.jpg" }
    }
}
