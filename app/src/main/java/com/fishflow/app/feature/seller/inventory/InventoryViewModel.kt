// InventoryViewModel.kt
// Developer: Fakhri
//
// ViewModel untuk monitoring inventory.
//
// Yang perlu dilakukan:
// - Saat init, subscribe ke Realtime listener produk milik penjual ini
// - Setiap ada perubahan stok/harga, UiState otomatis update
// - Fungsi onSearchQueryChange(query) → filter produk lokal
// - Fungsi onRefresh() → ambil ulang dari remote
// - Fungsi onToggleStatus(productId) → aktif/nonaktif-kan produk
