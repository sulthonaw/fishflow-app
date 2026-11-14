// RepositoryModule.kt
// Developer: Sulthon
//
// Koneksi: Semua repository perlu di-bind di sini. Sulthon yang wiring
// karena dia handle DI secara keseluruhan.
//
// Hilt module untuk menghubungkan interface Repository dengan implementasinya.
//
// Yang perlu dilakukan:
// - Anotasi @Module dan @InstallIn(SingletonComponent::class)
// - Bind AuthRepository -> AuthRepositoryImpl
// - Bind FishCatchRepository -> FishCatchRepositoryImpl
// - Bind ProductRepository -> ProductRepositoryImpl
// - Bind OrderRepository -> OrderRepositoryImpl
// - Bind DestinationRepository -> DestinationRepositoryImpl
// - Semua pakai @Binds dan @Singleton
