package com.fishflow.app.domain.usecase.tourism

import com.fishflow.app.data.model.Destination
import com.fishflow.app.data.repository.DestinationRepository

// GetDestinationsUseCase.kt
// Developer: Fatih
// Ambil daftar destinasi wisata. Bisa semua atau filter per wilayah.
// Pakai DestinationRepository.
class GetDestinationsUseCase(
    private val repository: DestinationRepository
) {
    suspend operator fun invoke(wilayah: String? = null): Result<List<Destination>> =
        if (wilayah.isNullOrBlank()) {
            repository.getAll()
        } else {
            repository.getByRegion(wilayah)
        }
}
