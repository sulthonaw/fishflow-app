// SearchDestinationsUseCase.kt
// Developer: Octavia
//
// Mencari destinasi dari kata kunci pencarian beranda. Fungsi murni tanpa akses data.
// Destinasi cocok jika nama atau wilayahnya mengandung kata kunci.
// Kata kunci kosong tidak menghasilkan destinasi.
package com.fishflow.app.domain.usecase.tourism

import com.fishflow.app.data.model.Destination

class SearchDestinationsUseCase {

    operator fun invoke(destinations: List<Destination>, query: String): List<Destination> {
        val keyword = query.trim()
        if (keyword.isEmpty()) return emptyList()

        return destinations.filter { destination ->
            destination.nama.contains(keyword, ignoreCase = true) ||
                destination.wilayah.contains(keyword, ignoreCase = true)
        }
    }
}
