// AppModule.kt
// Developer: Sulthon
//
// Koneksi: Sulthon setup Supabase client dan seluruh infrastruktur,
// jadi dia yang provide dependency-nya lewat Hilt di sini.
//
// Hilt module untuk menyediakan dependency yang bersifat app-wide (singleton).
//
// Yang perlu dilakukan:
// - Anotasi @Module dan @InstallIn(SingletonComponent::class)
// - Provide SupabaseClient singleton (pakai URL dan anon key dari SupabaseConfig)
// - Provide Dispatchers (IO, Main, Default) supaya bisa di-inject dan mudah di-test
// - Provide DataStoreManager untuk preferences lokal
