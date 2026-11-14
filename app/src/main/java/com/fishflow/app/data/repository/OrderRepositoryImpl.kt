// OrderRepositoryImpl.kt
// Developer: Sulthon
//
// Implementasi OrderRepository.
// - Inject OrderRemoteDataSource
// - createOrder() → validasi stok awal, simpan pesanan + items
// - confirmOrder() → validasi stok ulang (tahap 2), alokasi stok, update status
// - completeOrder() → kurangi stok final, catat penjualan selesai
// - observeOrderStatus() → Realtime listener untuk tracking status
//
// PENTING: confirmOrder harus atomic (pakai RPC/transaction di Supabase)
// supaya tidak terjadi overselling jika 2 pembeli pesan stok yang sama.
