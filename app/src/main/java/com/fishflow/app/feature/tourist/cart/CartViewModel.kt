// CartViewModel.kt
// Developer: Sulthon
//
// ViewModel keranjang belanja.
//
// Yang perlu dilakukan:
// - Simpan daftar item keranjang (bisa pakai state lokal atau DataStore)
// - Fungsi onAddItem(product, qty) → tambah ke keranjang
// - Fungsi onUpdateQuantity(productId, newQty) → update jumlah
// - Fungsi onRemoveItem(productId) → hapus dari keranjang
// - Fungsi onCheckout() →
//   1. Validasi stok tahap 1: cek apakah semua item stoknya masih cukup
//   2. Jika ada yang kurang → tampilkan error per item
//   3. Jika semua cukup → navigasi ke CheckoutScreen
// - Auto-kalkulasi totalHarga setiap kali items berubah
