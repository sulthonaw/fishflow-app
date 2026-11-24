// ProductStockSource.kt
// Developer: Octavia
//
// Koneksi: Kontrak baca stok antara katalog (Fungsi 4) dan perhitungan stok Fungsi 1.
// Katalog hanya membaca angka jadi dari sini dan tidak menghitung stok sendiri.
//
// Bentuk data mengikuti rencana CalculateAvailableStockUseCase (Fakhri):
// Map<productId, stok tersedia dalam kg>.
package com.fishflow.app.data.repository

interface ProductStockSource {

    suspend fun getAvailableStock(): Result<Map<String, Double>>
}

// SEMENTARA: angka contoh sampai CalculateAvailableStockUseCase milik Fungsi 1 selesai.
// Setelah itu, buat implementasi yang memanggil use case tersebut lalu pasang di
// ProductRepositoryImpl. Bagian katalog lainnya tidak perlu diubah.
object InMemoryProductStockSource : ProductStockSource {

    private val availableStock = mapOf(
        "prod-tuna-01" to 18.0,
        "prod-kakap-02" to 6.0,
        "prod-cumi-03" to 12.5,
        "prod-lobster-04" to 3.0,
        "prod-tongkol-05" to 25.0,
        "prod-kerapu-06" to 0.0,
        "prod-layur-07" to 10.0,
        "prod-udang-08" to 0.0
    )

    override suspend fun getAvailableStock(): Result<Map<String, Double>> =
        Result.success(availableStock)
}
