// SellerDashboardViewModel.kt
// Developer: Fakhri
//
// ViewModel dashboard penjual.
//
// Yang perlu dilakukan:
// - Saat init, ambil data ringkasan: total stok aktif, jumlah pesanan baru, omset
// - Data dari: ProductRepository (stok), OrderRepository (pesanan baru, omset)
// - Expose sebagai SellerDashboardUiState
// - Fungsi onRefresh() untuk pull-to-refresh
// - Bisa pakai Realtime listener supaya angka update otomatis tanpa refresh manual
