// DestinationRepositoryImpl.kt
// Developer: Fatih
//
// Implementasi DestinationRepository.
// - Inject DestinationRemoteDataSource dan DestinationDao (cache)
// - getAll() → ambil dari remote, cache, return
// - getActivities() → ambil aktivitas + biaya dari remote
// - Data destinasi jarang berubah, jadi cache lokal cukup berguna di sini
