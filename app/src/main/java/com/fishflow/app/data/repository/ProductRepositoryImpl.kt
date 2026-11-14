// ProductRepositoryImpl.kt
// Developer: Octavia
//
// Koneksi: Implementasi repositori produk ikan dikerjakan oleh Octavia untuk mendukung
// use case katalog wilayah, integrasi cache lokal produk, dan sinkronisasi data Supabase.
//
// Implementasi ProductRepository.
// - Inject ProductRemoteDataSource dan ProductDao (cache lokal)
// - getActiveProducts() -> ambil dari remote, cache ke lokal, return data
// - getByRegion() -> filter dari remote berdasarkan wilayah
// - observeProducts() -> pakai Supabase Realtime, emit setiap ada perubahan stok/harga
// - updatePrice() -> update di remote, invalidate cache lokal
