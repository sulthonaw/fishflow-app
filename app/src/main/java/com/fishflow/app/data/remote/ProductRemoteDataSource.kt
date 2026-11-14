// ProductRemoteDataSource.kt
// Developer: Octavia
//
// Koneksi: Octavia memegang fitur penemuan produk ikan berdasarkan wilayah wisata (Fungsi 4).
// DataSource ini mengurus query produk aktif, filter kawasan, serta listener realtime
// yang dipakai langsung di katalog wisatawan.
//
// Akses ke tabel "products" di Supabase.
//
// Yang perlu dilakukan:
// - getActiveProducts() -> ambil semua produk dengan status = aktif dan stok > 0
// - getProductsByRegion(wilayah) -> filter produk berdasarkan wilayah wisata
// - getProductsBySeller(sellerId) -> ambil produk milik penjual tertentu (untuk inventory)
// - getProductById(id) -> detail satu produk
// - updatePrice(productId, hargaBaru) -> perbarui harga
// - updateStock(productId, stokBaru) -> perbarui stok setelah tangkapan/penjualan
// - updateStatus(productId, aktif/nonaktif) -> toggle status produk
// - subscribeToProductChanges() -> Supabase Realtime listener untuk update stok otomatis
//
// Pakai supabase.postgrest untuk CRUD, supabase.realtime untuk listener.
