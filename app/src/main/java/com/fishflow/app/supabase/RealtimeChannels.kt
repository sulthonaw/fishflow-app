// RealtimeChannels.kt
// Developer: Sulthon
//
// Koneksi: Sulthon pakai Realtime untuk tracking order status di fiturnya.
// Dia juga yang setup Supabase client termasuk plugin Realtime.
//
// Definisi nama channel Realtime Supabase.
//
// Channel yang dibutuhkan:
// - PRODUCTS_CHANNEL -> listen perubahan tabel products (stok, harga)
// - ORDERS_CHANNEL -> listen perubahan tabel orders (status pesanan)
// - Bisa per-user: "orders:{userId}" supaya penjual hanya dengar pesanan miliknya
