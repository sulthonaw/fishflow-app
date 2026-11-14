// SupabaseClientProvider.kt
// Developer: Sulthon
//
// Koneksi: Sulthon yang handle seluruh koneksi Supabase (config, tabel, realtime).
// Client ini fondasi yang dipakai semua remote data source.
//
// File ini membuat instance Supabase Client yang dipakai seluruh aplikasi.
//
// Yang perlu dilakukan:
// - Buat fungsi yang return SupabaseClient menggunakan createSupabaseClient()
// - Install plugin: Auth, Postgrest, Realtime, Storage
// - Ambil URL dan anon key dari SupabaseConfig.kt
// - Client ini di-provide sebagai Singleton lewat AppModule
