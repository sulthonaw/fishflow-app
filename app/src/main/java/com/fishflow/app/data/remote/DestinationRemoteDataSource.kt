package com.fishflow.app.data.remote

import com.fishflow.app.core.network.SupabaseClientProvider
import com.fishflow.app.data.model.Activity
import com.fishflow.app.data.model.Destination
import com.fishflow.app.supabase.Tables
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

// DestinationRemoteDataSource.kt
// Developer: Fatih
//
// Akses ke tabel "destinations" dan "activities" di Supabase.
//
// Untuk tahap ini implementasi yang dipakai adalah penyimpanan in-memory
// (InMemoryDestinationRemoteDataSource) supaya fitur F3 bisa dijalankan dan
// diuji tanpa Hilt/Supabase. Ketika infrastruktur Supabase sudah siap,
// cukup buat implementasi lain dari interface yang sama.
interface DestinationRemoteDataSource {

    suspend fun getAllDestinations(): List<Destination>

    suspend fun getDestinationsByRegion(wilayah: String): List<Destination>

    suspend fun getDestinationById(id: String): Destination?

    suspend fun getActivitiesByDestination(destinationId: String): List<Activity>

    suspend fun getActivityById(id: String): Activity?

    // Operasi admin
    suspend fun insertDestination(destination: Destination): Destination

    suspend fun updateDestination(destination: Destination): Destination

    suspend fun deleteDestination(id: String): Boolean

    suspend fun insertActivity(activity: Activity): Activity

    suspend fun updateActivity(activity: Activity): Activity

    suspend fun deleteActivity(id: String): Boolean
}

// Data contoh kawasan pesisir Kabupaten Blitar. Aktivitas dengan biaya null
// sengaja disertakan untuk menguji aturan "biaya belum tersedia".
object InMemoryDestinationRemoteDataSource : DestinationRemoteDataSource {

    private val destinations = mutableListOf<Destination>()
    private val activities = mutableListOf<Activity>()

    init {
        resetToSeed()
    }

    @Synchronized
    fun resetToSeed() {
        destinations.clear()
        activities.clear()
        destinations.addAll(seedDestinations())
        activities.addAll(seedActivities())
    }

    override suspend fun getAllDestinations(): List<Destination> =
        synchronized(this) { destinations.sortedBy { it.nama } }

    override suspend fun getDestinationsByRegion(wilayah: String): List<Destination> =
        synchronized(this) {
            destinations
                .filter { it.wilayah.equals(wilayah, ignoreCase = true) }
                .sortedBy { it.nama }
        }

    override suspend fun getDestinationById(id: String): Destination? =
        synchronized(this) { destinations.firstOrNull { it.id == id } }

    override suspend fun getActivitiesByDestination(destinationId: String): List<Activity> =
        synchronized(this) { activities.filter { it.destinationId == destinationId } }

    override suspend fun getActivityById(id: String): Activity? =
        synchronized(this) { activities.firstOrNull { it.id == id } }

    override suspend fun insertDestination(destination: Destination): Destination =
        synchronized(this) {
            val newDestination = destination.copy(
                id = destination.id.ifBlank { UUID.randomUUID().toString() }
            )
            destinations.add(newDestination)
            newDestination
        }

    override suspend fun updateDestination(destination: Destination): Destination =
        synchronized(this) {
            val index = destinations.indexOfFirst { it.id == destination.id }
            require(index >= 0) { "Destinasi tidak ditemukan" }
            destinations[index] = destination
            destination
        }

    override suspend fun deleteDestination(id: String): Boolean =
        synchronized(this) {
            val removed = destinations.removeAll { it.id == id }
            if (removed) {
                activities.removeAll { it.destinationId == id }
            }
            removed
        }

    override suspend fun insertActivity(activity: Activity): Activity =
        synchronized(this) {
            val newActivity = activity.copy(
                id = activity.id.ifBlank { UUID.randomUUID().toString() }
            )
            activities.add(newActivity)
            newActivity
        }

    override suspend fun updateActivity(activity: Activity): Activity =
        synchronized(this) {
            val index = activities.indexOfFirst { it.id == activity.id }
            require(index >= 0) { "Aktivitas tidak ditemukan" }
            activities[index] = activity
            activity
        }

    override suspend fun deleteActivity(id: String): Boolean =
        synchronized(this) { activities.removeAll { it.id == id } }

    private fun seedDestinations(): List<Destination> = listOf(
        Destination(
            id = "dest-tambakrejo",
            nama = "Pantai Tambakrejo",
            wilayah = "Tambakrejo",
            deskripsi = "Pantai landai di pesisir selatan Blitar dengan deretan warung " +
                "ikan bakar dan area parkir yang luas. Cocok untuk wisata keluarga.",
            latitude = -8.2219,
            longitude = 112.1416
        ),
        Destination(
            id = "dest-serang",
            nama = "Pantai Serang",
            wilayah = "Serang",
            deskripsi = "Pantai berpasir putih dengan tebing hijau dan spot foto " +
                "yang populer. Tersedia penyewaan perahu untuk melihat pantai dari laut.",
            latitude = -8.2113,
            longitude = 112.0899
        ),
        Destination(
            id = "dest-jolosutro",
            nama = "Pantai Jolosutro",
            wilayah = "Jolosutro",
            deskripsi = "Pantai yang relatif tenang dengan area camping dan " +
                "kuliner seafood hasil tangkapan nelayan setempat.",
            latitude = -8.2431,
            longitude = 112.1964
        )
    )

    private fun seedActivities(): List<Activity> = listOf(
        Activity(
            id = "act-tambakrejo-retribusi",
            destinationId = "dest-tambakrejo",
            nama = "Retribusi Pantai",
            kategori = "Retribusi",
            biaya = 5_000.0
        ),
        Activity(
            id = "act-tambakrejo-parkir",
            destinationId = "dest-tambakrejo",
            nama = "Parkir Motor",
            kategori = "Transportasi",
            biaya = 3_000.0
        ),
        Activity(
            id = "act-tambakrejo-perahu",
            destinationId = "dest-tambakrejo",
            nama = "Sewa Perahu",
            kategori = "Sewa Perahu",
            biaya = 150_000.0
        ),
        Activity(
            id = "act-tambakrejo-kuliner",
            destinationId = "dest-tambakrejo",
            nama = "Kuliner Ikan Bakar",
            kategori = "Kuliner",
            biaya = 45_000.0
        ),
        Activity(
            id = "act-tambakrejo-pemandu",
            destinationId = "dest-tambakrejo",
            nama = "Pemandu Lokal",
            kategori = "Pemandu",
            biaya = null,
            isAvailable = false
        ),
        Activity(
            id = "act-serang-retribusi",
            destinationId = "dest-serang",
            nama = "Retribusi Pantai",
            kategori = "Retribusi",
            biaya = 5_000.0
        ),
        Activity(
            id = "act-serang-parkir",
            destinationId = "dest-serang",
            nama = "Parkir Mobil",
            kategori = "Transportasi",
            biaya = 5_000.0
        ),
        Activity(
            id = "act-serang-perahu",
            destinationId = "dest-serang",
            nama = "Sewa Perahu",
            kategori = "Sewa Perahu",
            biaya = null,
            isAvailable = false
        ),
        Activity(
            id = "act-serang-snorkeling",
            destinationId = "dest-serang",
            nama = "Snorkeling",
            kategori = "Aktivitas Air",
            biaya = 100_000.0
        ),
        Activity(
            id = "act-jolosutro-retribusi",
            destinationId = "dest-jolosutro",
            nama = "Retribusi Pantai",
            kategori = "Retribusi",
            biaya = 0.0
        ),
        Activity(
            id = "act-jolosutro-parkir",
            destinationId = "dest-jolosutro",
            nama = "Parkir Motor",
            kategori = "Transportasi",
            biaya = 2_000.0
        ),
        Activity(
            id = "act-jolosutro-tenda",
            destinationId = "dest-jolosutro",
            nama = "Sewa Tenda Camping",
            kategori = "Sewa Peralatan",
            biaya = 50_000.0
        ),
        Activity(
            id = "act-jolosutro-kuliner",
            destinationId = "dest-jolosutro",
            nama = "Kuliner Seafood",
            kategori = "Kuliner",
            biaya = null,
            isAvailable = false
        )
    )
}

@Serializable
private data class DestinationDto(
    val id: String = "",
    val nama: String = "",
    val wilayah: String = "",
    val deskripsi: String = "",
    @SerialName("foto_url") val fotoUrl: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
) {
    fun toDomain(): Destination = Destination(
        id = id,
        nama = nama,
        wilayah = wilayah,
        deskripsi = deskripsi,
        fotoUrl = fotoUrl,
        latitude = latitude,
        longitude = longitude
    )
}

@Serializable
private data class DestinationPayload(
    val nama: String,
    val wilayah: String,
    val deskripsi: String,
    @SerialName("foto_url") val fotoUrl: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)

@Serializable
private data class ActivityDto(
    val id: String = "",
    @SerialName("destination_id") val destinationId: String = "",
    val nama: String = "",
    val kategori: String = "",
    val biaya: Double? = null,
    @SerialName("is_available") val isAvailable: Boolean = true
) {
    fun toDomain(): Activity = Activity(
        id = id,
        destinationId = destinationId,
        nama = nama,
        kategori = kategori,
        biaya = biaya,
        isAvailable = isAvailable
    )
}

@Serializable
private data class ActivityPayload(
    @SerialName("destination_id") val destinationId: String,
    val nama: String,
    val kategori: String,
    val biaya: Double? = null,
    @SerialName("is_available") val isAvailable: Boolean = true
)

class SupabaseDestinationRemoteDataSource(
    private val supabase: SupabaseClient = SupabaseClientProvider.client
) : DestinationRemoteDataSource {

    override suspend fun getAllDestinations(): List<Destination> =
        supabase.postgrest[Tables.DESTINATIONS]
            .select {
                order("nama", Order.ASCENDING)
            }
            .decodeList<DestinationDto>()
            .map { it.toDomain() }

    override suspend fun getDestinationsByRegion(wilayah: String): List<Destination> =
        supabase.postgrest[Tables.DESTINATIONS]
            .select {
                filter {
                    ilike("wilayah", wilayah.trim())
                }
                order("nama", Order.ASCENDING)
            }
            .decodeList<DestinationDto>()
            .map { it.toDomain() }

    override suspend fun getDestinationById(id: String): Destination? =
        supabase.postgrest[Tables.DESTINATIONS]
            .select {
                filter {
                    eq("id", id)
                }
            }
            .decodeSingleOrNull<DestinationDto>()
            ?.toDomain()

    override suspend fun getActivitiesByDestination(destinationId: String): List<Activity> =
        supabase.postgrest[Tables.ACTIVITIES]
            .select {
                filter {
                    eq("destination_id", destinationId)
                }
                order("nama", Order.ASCENDING)
            }
            .decodeList<ActivityDto>()
            .map { it.toDomain() }

    override suspend fun getActivityById(id: String): Activity? =
        supabase.postgrest[Tables.ACTIVITIES]
            .select {
                filter {
                    eq("id", id)
                }
            }
            .decodeSingleOrNull<ActivityDto>()
            ?.toDomain()

    override suspend fun insertDestination(destination: Destination): Destination {
        val payload = DestinationPayload(
            nama = destination.nama,
            wilayah = destination.wilayah,
            deskripsi = destination.deskripsi,
            fotoUrl = destination.fotoUrl,
            latitude = destination.latitude,
            longitude = destination.longitude
        )
        return supabase.postgrest[Tables.DESTINATIONS]
            .insert(payload) { select() }
            .decodeSingle<DestinationDto>()
            .toDomain()
    }

    override suspend fun updateDestination(destination: Destination): Destination {
        val payload = DestinationPayload(
            nama = destination.nama,
            wilayah = destination.wilayah,
            deskripsi = destination.deskripsi,
            fotoUrl = destination.fotoUrl,
            latitude = destination.latitude,
            longitude = destination.longitude
        )
        return supabase.postgrest[Tables.DESTINATIONS]
            .update(payload) {
                select()
                filter { eq("id", destination.id) }
            }
            .decodeSingle<DestinationDto>()
            .toDomain()
    }

    override suspend fun deleteDestination(id: String): Boolean {
        supabase.postgrest[Tables.DESTINATIONS]
            .delete {
                filter { eq("id", id) }
            }
        return true
    }

    override suspend fun insertActivity(activity: Activity): Activity {
        val payload = ActivityPayload(
            destinationId = activity.destinationId,
            nama = activity.nama,
            kategori = activity.kategori,
            biaya = activity.biaya,
            isAvailable = activity.isAvailable
        )
        return supabase.postgrest[Tables.ACTIVITIES]
            .insert(payload) { select() }
            .decodeSingle<ActivityDto>()
            .toDomain()
    }

    override suspend fun updateActivity(activity: Activity): Activity {
        val payload = ActivityPayload(
            destinationId = activity.destinationId,
            nama = activity.nama,
            kategori = activity.kategori,
            biaya = activity.biaya,
            isAvailable = activity.isAvailable
        )
        return supabase.postgrest[Tables.ACTIVITIES]
            .update(payload) {
                select()
                filter { eq("id", activity.id) }
            }
            .decodeSingle<ActivityDto>()
            .toDomain()
    }

    override suspend fun deleteActivity(id: String): Boolean {
        supabase.postgrest[Tables.ACTIVITIES]
            .delete {
                filter { eq("id", id) }
            }
        return true
    }
}
