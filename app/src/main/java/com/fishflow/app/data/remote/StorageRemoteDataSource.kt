
package com.fishflow.app.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.upload
import java.util.UUID

class StorageRemoteDataSource(
    private val supabase: SupabaseClient
) {

    companion object {
        private const val CATCH_BUCKET = "catch-photos"
        private const val PRODUCT_BUCKET = "product-images"
    }

    /**
     * Upload foto hasil tangkapan nelayan.
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

        val filePath =
            "$safeSellerId/${UUID.randomUUID()}_$safeFileName"

        val bucket = supabase.storage.from(CATCH_BUCKET)

        bucket.upload(path = filePath, data = imageBytes) {
            upsert = false
        }

        return bucket.publicUrl(filePath)
    }

    /**
     * Upload gambar produk ikan.
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

        val filePath =
            "$safeProductId/${UUID.randomUUID()}.jpg"

        val bucket = supabase.storage.from(PRODUCT_BUCKET)

        bucket.upload(path = filePath, data = imageBytes) {
            upsert = false
        }

        return bucket.publicUrl(filePath)
    }

    /**
     * Mengambil URL publik sebuah gambar.
     */
    fun getPublicUrl(
        bucket: String,
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
        bucket: String,
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
