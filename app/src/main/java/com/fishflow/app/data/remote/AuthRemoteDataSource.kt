// AuthRemoteDataSource.kt
// Developer: Fakhri
//
// Koneksi: Fakhri handle seluruh alur auth (login/register screen, model User,
// validasi input). Remote data source ini backend-nya.
//
// Akses ke Supabase Auth.
//
// Yang perlu dilakukan:
// - signUp(email, password, nama, role) -> daftarkan user baru, simpan metadata role
// - signIn(email, password) -> login, return session
// - signOut() -> logout, hapus session
// - getCurrentSession() -> ambil session aktif (null jika belum login)
// - getCurrentUser() -> ambil data user yang sedang login
// - onAuthStateChange() -> Flow yang emit setiap kali status auth berubah
//
// Semua fungsi pakai Supabase Auth plugin (supabase.auth).
