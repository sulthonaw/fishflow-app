// DestinationRemoteDataSource.kt
// Developer: Fatih
//
// Akses ke tabel "destinations" dan "activities" di Supabase.
//
// Yang perlu dilakukan:
// - getAllDestinations() → ambil semua destinasi wisata
// - getDestinationsByRegion(wilayah) → filter destinasi per wilayah
// - getDestinationById(id) → detail satu destinasi
// - getActivitiesByDestination(destinationId) → daftar aktivitas + biaya di destinasi tsb
// - Untuk admin: insertDestination(), updateDestination(), deleteDestination()
//
// Pakai supabase.postgrest.
