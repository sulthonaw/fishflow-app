// CompleteOrderUseCase.kt
// Developer: Sulthon
//
// Use case saat serah terima ikan selesai di dermaga/TPI.
// - Update status jadi SELESAI
// - Kurangi stok final (stokTeralokasi berkurang, stokTersedia tetap)
// - Catat ke riwayat penjualan
// - Generate kode pickup/token jika belum ada
