// AuthRepositoryImpl.kt
// Developer: Fakhri
//
// Koneksi: Implementasi auth repository. Fakhri handle auth dari screen sampai data layer.
// Ini menghubungkan AuthRemoteDataSource (Supabase Auth) dengan DataStoreManager (lokal).
//
// Yang perlu dilakukan:
// - Inject AuthRemoteDataSource dan DataStoreManager
// - login() -> panggil remote signIn, simpan role ke DataStore
// - register() -> panggil remote signUp, buat row di tabel "users"
// - logout() -> panggil remote signOut, hapus data lokal
// - getCurrentUser() -> cek session, return User
// - observeAuthState() -> forward Flow dari remote onAuthStateChange
