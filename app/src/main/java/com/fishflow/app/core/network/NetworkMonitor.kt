// NetworkMonitor.kt
// Developer: Sulthon
//
// Koneksi: Bagian dari infrastruktur jaringan. Sulthon yang setup koneksi,
// jadi monitoring koneksi juga tanggung jawabnya.
//
// Pemantau koneksi internet.
//
// Yang perlu dilakukan:
// - Pakai ConnectivityManager untuk cek apakah device online/offline
// - Expose sebagai Flow<Boolean> supaya UI bisa react saat koneksi putus
// - Tampilkan banner/snackbar di UI saat offline (opsional, bisa nanti)
