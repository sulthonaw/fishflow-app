
package com.fishflow.app.data.model

data class FishProduct(
    val id: String = "",
    val sellerId: String = "",
    val namaIkan: String = "",
    val hargaPerKg: Double = 0.0,
    val stokTersedia: Double = 0.0,
    val stokTeralokasi: Double = 0.0,
    val status: Boolean = true,
    val wilayah: String = ""
) {
    // Menghitung stok yang masih dapat dipesan
    val stokRiil: Double
        get() = (stokTersedia - stokTeralokasi).coerceAtLeast(0.0)

    // Menghitung apakah produk masih tersedia
    val isAvailable: Boolean
        get() = status && stokRiil > 0.0
}
