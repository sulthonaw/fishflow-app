// ConfirmOrderUseCase.kt
// Developer: Sulthon
//
// Use case saat penjual menerima/konfirmasi pesanan.
//
// Langkah:
// 1. Cek stok tahap 2 (validasi ulang, antisipasi race condition)
// 2. Jika stok masih cukup → alokasikan stok (kunci supaya tidak bisa dipesan orang lain)
// 3. Update status pesanan jadi DIKONFIRMASI
// 4. Jika stok ternyata sudah habis → tolak otomatis, beri alasan "stok habis"
// 5. Return Result<Order>
//
// HARUS atomic — pakai Supabase RPC/database function.
