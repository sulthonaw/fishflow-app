// FishCatalogViewModel.kt
// Developer: Octavia
//
// ViewModel katalog ikan untuk wisatawan.
//
// Yang perlu dilakukan:
// - Saat init, ambil semua produk aktif dengan stok > 0
// - Subscribe Realtime listener supaya stok update otomatis
// - Fungsi onRegionFilter(wilayah) → filter produk berdasarkan wilayah wisata
// - Fungsi onClearFilter() → tampilkan semua produk
// - Fungsi onSearchChange(query) → cari berdasarkan nama ikan
// - Jika hasil filter kosong → set isEmpty = true
