// FishCatchRepositoryImpl.kt
// Developer: Fakhri
//
// Implementasi FishCatchRepository.
// - Inject FishCatchRemoteDataSource dan StorageRemoteDataSource
// - recordCatch() → upload foto (jika ada) lalu insert data tangkapan ke Supabase
// - getCatchesBySeller() → ambil data dari remote
// - Setelah catat tangkapan, perlu update stok di tabel products juga
