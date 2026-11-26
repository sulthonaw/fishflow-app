package com.fishflow.app.data.repository

import com.fishflow.app.data.model.Activity
import com.fishflow.app.data.model.Destination
import com.fishflow.app.data.remote.DestinationRemoteDataSource
import com.fishflow.app.data.remote.InMemoryDestinationRemoteDataSource
import com.fishflow.app.data.remote.SupabaseDestinationRemoteDataSource

// DestinationRepositoryImpl.kt
// Developer: Fatih
//
// Implementasi DestinationRepository di atas DestinationRemoteDataSource.
// Untuk sementara data disimpan in-memory (lihat InMemoryDestinationRemoteDataSource),
// jadi belum ada cache Room. Semua error dibungkus Result supaya ViewModel bisa
// menampilkan pesan yang ramah pengguna.
class DestinationRepositoryImpl(
    private val remote: DestinationRemoteDataSource = InMemoryDestinationRemoteDataSource
) : DestinationRepository {

    override suspend fun getAll(): Result<List<Destination>> = runCatching {
        remote.getAllDestinations()
    }

    override suspend fun getByRegion(wilayah: String): Result<List<Destination>> = runCatching {
        if (wilayah.isBlank()) {
            remote.getAllDestinations()
        } else {
            remote.getDestinationsByRegion(wilayah.trim())
        }
    }

    override suspend fun getById(id: String): Result<Destination> = runCatching {
        require(id.isNotBlank()) { "ID destinasi tidak valid" }
        remote.getDestinationById(id) ?: error("Destinasi tidak ditemukan")
    }

    override suspend fun getActivities(destinationId: String): Result<List<Activity>> = runCatching {
        require(destinationId.isNotBlank()) { "ID destinasi tidak valid" }
        remote.getActivitiesByDestination(destinationId)
    }

    override suspend fun createDestination(destination: Destination): Result<Destination> = runCatching {
        validateDestination(destination)
        remote.insertDestination(destination.normalized())
    }

    override suspend fun updateDestination(destination: Destination): Result<Destination> = runCatching {
        require(destination.id.isNotBlank()) { "ID destinasi tidak valid" }
        validateDestination(destination)
        remote.updateDestination(destination.normalized())
    }

    override suspend fun deleteDestination(id: String): Result<Unit> = runCatching {
        require(id.isNotBlank()) { "ID destinasi tidak valid" }
        check(remote.deleteDestination(id)) { "Destinasi tidak ditemukan" }
    }

    override suspend fun createActivity(activity: Activity): Result<Activity> = runCatching {
        validateActivity(activity)
        require(remote.getDestinationById(activity.destinationId) != null) {
            "Destinasi tidak ditemukan"
        }
        remote.insertActivity(activity.normalized())
    }

    override suspend fun updateActivity(activity: Activity): Result<Activity> = runCatching {
        require(activity.id.isNotBlank()) { "ID aktivitas tidak valid" }
        validateActivity(activity)
        remote.updateActivity(activity.normalized())
    }

    override suspend fun deleteActivity(id: String): Result<Unit> = runCatching {
        require(id.isNotBlank()) { "ID aktivitas tidak valid" }
        check(remote.deleteActivity(id)) { "Aktivitas tidak ditemukan" }
    }

    private fun validateDestination(destination: Destination) {
        require(destination.nama.isNotBlank()) { "Nama destinasi wajib diisi" }
        require(destination.wilayah.isNotBlank()) { "Wilayah wajib diisi" }
        require(destination.deskripsi.isNotBlank()) { "Deskripsi wajib diisi" }
        destination.latitude?.let {
            require(it in -90.0..90.0) { "Latitude harus di antara -90 dan 90" }
        }
        destination.longitude?.let {
            require(it in -180.0..180.0) { "Longitude harus di antara -180 dan 180" }
        }
    }

    private fun validateActivity(activity: Activity) {
        require(activity.nama.isNotBlank()) { "Nama aktivitas wajib diisi" }
        require(activity.kategori.isNotBlank()) { "Kategori aktivitas wajib diisi" }
        val biaya = activity.biaya
        if (activity.isAvailable) {
            require(biaya != null) { "Biaya wajib diisi untuk aktivitas yang tersedia" }
            require(biaya >= 0.0) { "Biaya aktivitas tidak boleh negatif" }
        }
    }

    private fun Destination.normalized(): Destination = copy(
        nama = nama.trim(),
        wilayah = wilayah.trim(),
        deskripsi = deskripsi.trim(),
        fotoUrl = fotoUrl?.trim()?.takeIf { it.isNotBlank() }
    )

    private fun Activity.normalized(): Activity = copy(
        nama = nama.trim(),
        kategori = kategori.trim(),
        biaya = if (isAvailable) biaya else null,
        isAvailable = isAvailable && biaya != null
    )

    companion object {
        fun default(): DestinationRepository = DestinationRepositoryImpl()

        fun supabase(): DestinationRepository = DestinationRepositoryImpl(
            remote = SupabaseDestinationRemoteDataSource()
        )
    }
}
