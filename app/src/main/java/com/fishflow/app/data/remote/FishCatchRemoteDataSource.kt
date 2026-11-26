package com.fishflow.app.data.remote

import com.fishflow.app.core.network.SupabaseClientProvider
import com.fishflow.app.data.model.FishCatch
import com.fishflow.app.supabase.Tables
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CatchDto(
    val id: String = "",
    @SerialName("seller_id") val sellerId: String,
    @SerialName("nama_ikan") val namaIkan: String,
    @SerialName("berat_kg") val beratKg: Double,
    @SerialName("tanggal_tangkap") val tanggalTangkap: String,
    @SerialName("foto_url") val fotoUrl: String? = null,
    @SerialName("created_at") val createdAt: String = ""
) {
    fun toDomain(): FishCatch = FishCatch(
        id = id,
        sellerId = sellerId,
        namaIkan = namaIkan,
        beratKg = beratKg,
        tanggalTangkap = tanggalTangkap,
        fotoUrl = fotoUrl,
        createdAt = createdAt
    )
}

@Serializable
private data class InsertCatchPayload(
    @SerialName("seller_id") val sellerId: String,
    @SerialName("nama_ikan") val namaIkan: String,
    @SerialName("berat_kg") val beratKg: Double,
    @SerialName("tanggal_tangkap") val tanggalTangkap: String,
    @SerialName("foto_url") val fotoUrl: String? = null
)

class FishCatchRemoteDataSource(
    private val supabase: SupabaseClient = SupabaseClientProvider.client
) {

    suspend fun insertCatch(
        sellerId: String,
        jenisIkan: String,
        beratKg: Double,
        tanggal: String,
        fotoUrl: String? = null
    ): FishCatch {
        require(sellerId.isNotBlank()) { "Seller ID tidak boleh kosong." }
        require(jenisIkan.isNotBlank()) { "Jenis ikan harus diisi." }
        require(beratKg > 0) { "Berat tangkapan harus lebih dari 0 kg." }

        val payload = InsertCatchPayload(
            sellerId = sellerId,
            namaIkan = jenisIkan.trim(),
            beratKg = beratKg,
            tanggalTangkap = tanggal.trim(),
            fotoUrl = fotoUrl
        )

        return supabase.postgrest[Tables.CATCHES]
            .insert(payload) {
                select()
            }
            .decodeSingle<CatchDto>()
            .toDomain()
    }

    suspend fun getCatchesBySeller(sellerId: String): List<FishCatch> {
        return supabase.postgrest[Tables.CATCHES]
            .select {
                filter {
                    eq("seller_id", sellerId)
                }
                order("tanggal_tangkap", Order.DESCENDING)
            }
            .decodeList<CatchDto>()
            .map { it.toDomain() }
    }

    suspend fun getCatchesByDate(
        sellerId: String,
        tanggal: String
    ): List<FishCatch> {
        return supabase.postgrest[Tables.CATCHES]
            .select {
                filter {
                    eq("seller_id", sellerId)
                    eq("tanggal_tangkap", tanggal)
                }
                order("created_at", Order.DESCENDING)
            }
            .decodeList<CatchDto>()
            .map { it.toDomain() }
    }

    suspend fun deleteCatch(catchId: String): Boolean {
        supabase.postgrest[Tables.CATCHES]
            .delete {
                filter {
                    eq("id", catchId)
                }
            }
        return true
    }

    suspend fun getAllCatches(): List<FishCatch> {
        return supabase.postgrest[Tables.CATCHES]
            .select {
                order("created_at", Order.DESCENDING)
            }
            .decodeList<CatchDto>()
            .map { it.toDomain() }
    }
}
