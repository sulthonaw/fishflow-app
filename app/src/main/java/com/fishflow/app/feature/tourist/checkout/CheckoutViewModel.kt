// CheckoutViewModel.kt
// Developer: Sulthon
//
// ViewModel halaman checkout.
//
// Yang perlu dilakukan:
// - Ambil data keranjang dari CartViewModel atau parameter navigasi
// - Hitung subtotal + biaya tambahan + total
// - Fungsi onConfirmOrder() →
//   1. Pastikan user sudah login (jika belum, arahkan ke login dulu)
//   2. Panggil CreateOrderUseCase
//   3. Jika berhasil → set isOrderCreated = true, simpan orderId
//   4. Navigasi ke OrderTrackingScreen
// - Fungsi onCancel() → kembali ke keranjang
