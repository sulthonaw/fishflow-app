// DataStoreManager.kt
// Developer: Sulthon
//
// Koneksi: DataStore di-provide lewat AppModule (Hilt) yang Sulthon buat.
// Dipakai untuk simpan session/role setelah login.
//
// Simpan preferences user pakai Jetpack DataStore.
//
// Yang perlu dilakukan:
// - Simpan/baca role user yang sedang login
// - Simpan/baca tema gelap/terang (jika ada)
// - Simpan/baca flag onboarding sudah selesai atau belum
// - Semua pakai Flow supaya UI otomatis update
