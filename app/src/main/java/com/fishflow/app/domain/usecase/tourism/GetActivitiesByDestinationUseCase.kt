package com.fishflow.app.domain.usecase.tourism

import com.fishflow.app.data.model.Activity
import com.fishflow.app.data.repository.DestinationRepository

// GetActivitiesByDestinationUseCase.kt
// Developer: Fatih
// Ambil daftar aktivitas wisata di destinasi tertentu beserta biayanya.
// Return List<Activity>, termasuk yang biayanya belum tersedia (isAvailable = false).
class GetActivitiesByDestinationUseCase(
    private val repository: DestinationRepository
) {
    suspend operator fun invoke(destinationId: String): Result<List<Activity>> {
        if (destinationId.isBlank()) {
            return Result.failure(IllegalArgumentException("ID destinasi tidak valid"))
        }
        return repository.getActivities(destinationId)
    }
}
