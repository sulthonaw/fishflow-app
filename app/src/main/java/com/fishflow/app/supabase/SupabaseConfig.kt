// SupabaseConfig.kt
// Developer: Sulthon
//
// Koneksi: Sulthon setup seluruh infrastruktur Supabase (client, tabel, realtime).
// Config ini dipakai oleh SupabaseClientProvider.kt yang juga dia buat.
//
// Konstanta konfigurasi Supabase.
//
// Yang perlu diisi:
// - SUPABASE_URL = "https://<project-id>.supabase.co"  <- dari Supabase Dashboard
// - SUPABASE_ANON_KEY = "eyJ..."  <- dari Settings > API > anon/public key
//
// PENTING: Jangan commit key ke Git publik.
// Idealnya simpan di local.properties atau BuildConfig, bukan hardcode.
