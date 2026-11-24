// OrderStatus.kt
// Developer: Sulthon
//
// Enum status pesanan. Urutannya:
// 1. MENUNGGU_KONFIRMASI — pesanan baru dibuat, nunggu penjual terima/tolak
// 2. DIKONFIRMASI — penjual sudah terima, stok dialokasikan
// 3. SIAP_DIAMBIL — ikan sudah disiapkan di dermaga/TPI
// 4. SELESAI — serah terima ikan sudah dilakukan
// 5. DITOLAK — penjual menolak pesanan
// 6. DIBATALKAN — wisatawan membatalkan sebelum dikonfirmasi
package com.fishflow.app.data.model

enum class OrderStatus(val label: String) {
    MENUNGGU_KONFIRMASI("Menunggu Konfirmasi"),
    DIKONFIRMASI("Dikonfirmasi"),
    SIAP_DIAMBIL("Siap Diambil"),
    SELESAI("Selesai"),
    DITOLAK("Ditolak"),
    DIBATALKAN("Dibatalkan")
}
