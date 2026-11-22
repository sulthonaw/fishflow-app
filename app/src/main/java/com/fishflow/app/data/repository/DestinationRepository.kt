package com.fishflow.app.data.repository

import com.fishflow.app.data.model.Activity
import com.fishflow.app.data.model.Destination

// DestinationRepository.kt
// Developer: Fatih
// Interface untuk destinasi wisata. Fungsi: getAll, getByRegion, getById, getActivities.
// Ditambah operasi admin: create, update, delete untuk destinasi dan aktivitas.
interface DestinationRepository {

    suspend fun getAll(): Result<List<Destination>>

    suspend fun getByRegion(wilayah: String): Result<List<Destination>>

    suspend fun getById(id: String): Result<Destination>

    suspend fun getActivities(destinationId: String): Result<List<Activity>>

    suspend fun createDestination(destination: Destination): Result<Destination>

    suspend fun updateDestination(destination: Destination): Result<Destination>

    suspend fun deleteDestination(id: String): Result<Unit>

    suspend fun createActivity(activity: Activity): Result<Activity>

    suspend fun updateActivity(activity: Activity): Result<Activity>

    suspend fun deleteActivity(id: String): Result<Unit>
}
