// Activity.kt
// Developer: Fatih
//
// Data class untuk aktivitas wisata di suatu destinasi.
// Field: id, destinationId, nama, biaya (Double, nullable jika belum tersedia),
//        kategori (String, misal: "Retribusi", "Sewa Perahu", "Kuliner"), isAvailable (Boolean)
// Mapping ke tabel "activities" di Supabase.
// Biaya bisa null kalau belum diketahui — nanti ditandai di estimasi anggaran.
