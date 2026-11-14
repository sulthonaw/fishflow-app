// DateFormatter.kt
// Developer: Fakhri
//
// Koneksi: Fakhri butuh format tanggal di form tangkapan (tanggalTangkap)
// dan di dashboard penjual. Fungsi ini juga dipakai fitur lain.
//
// Fungsi-fungsi untuk format tanggal yang konsisten di seluruh app.
//
// Yang perlu dilakukan:
// - Format tanggal ke "dd MMM yyyy" (misal: 13 Nov 2026)
// - Format tanggal + waktu ke "dd MMM yyyy, HH:mm"
// - Parse string tanggal dari Supabase (ISO 8601) ke LocalDate/LocalDateTime
// - Fungsi "tanggal relatif" misal: "Hari ini", "Kemarin", "3 hari lalu"
