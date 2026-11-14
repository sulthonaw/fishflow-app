// FishCatchRemoteDataSource.kt
// Developer: Fakhri
//
// Akses ke tabel "catches" di Supabase.
//
// Yang perlu dilakukan:
// - insertCatch(catch) → tambah catatan tangkapan baru
// - getCatchesBySeller(sellerId) → ambil semua tangkapan milik penjual tertentu
// - getCatchesByDate(sellerId, tanggal) → filter tangkapan per tanggal
// - deleteCatch(catchId) → hapus catatan (jika perlu koreksi)
//
// Pakai supabase.postgrest untuk query ke tabel.
