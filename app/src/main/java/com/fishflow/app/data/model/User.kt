// User.kt
// Developer: Fakhri
//
// Koneksi: Fakhri handle auth dan data model. User dipakai di login/register
// dan jadi basis identitas di seluruh fitur (siapa yang catat tangkapan, siapa yang pesan).
//
// Data class untuk user/pengguna.
// Field: id (String/UUID), email, nama, role (UserRole), fotoUrl (nullable), createdAt
// Mapping ke tabel "users" di Supabase.
