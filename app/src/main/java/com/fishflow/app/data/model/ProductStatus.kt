// ProductStatus.kt
// Developer: Octavia
//
// Enum status produk. Hanya produk AKTIF yang boleh tampil di katalog wisatawan.
package com.fishflow.app.data.model

enum class ProductStatus(val label: String) {
    AKTIF("Aktif"),
    NONAKTIF("Nonaktif")
}
