// CreateOrderUseCase.kt
// Developer: Sulthon
//
// Use case untuk membuat pesanan baru (sisi wisatawan).
//
// Langkah:
// 1. Cek user sudah login (harus role WISATAWAN)
// 2. Cek stok tahap 1: stok tersedia >= qty pesanan
// 3. Hitung total harga = sum(qty * harga per item)
// 4. Simpan pesanan dengan status MENUNGGU_KONFIRMASI
// 5. Return Result<Order>
//
// Kalau stok tidak cukup, return error dengan pesan yang jelas.
