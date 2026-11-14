// FishFlowDatabase.kt
// Developer: Sulthon
//
// Koneksi: Bagian infrastruktur data. Sulthon yang wiring DI dan data layer,
// jadi Room database juga dia yang setup.
//
// Room database untuk cache lokal (opsional, untuk pengalaman offline).
//
// Yang perlu dilakukan:
// - Anotasi @Database dengan entities dan version
// - Abstract class extends RoomDatabase
// - Definisikan abstract fun untuk setiap DAO
// - Untuk saat ini, cache destinasi dan produk saja sudah cukup
