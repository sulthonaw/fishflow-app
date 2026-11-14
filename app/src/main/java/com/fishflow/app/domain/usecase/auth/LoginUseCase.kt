// LoginUseCase.kt
// Developer: Fakhri
//
// Koneksi: Fakhri bikin LoginScreen dan LoginViewModel, jadi use case login juga dia.
// Setelah login berhasil, user diarahkan ke SellerDashboard (screen Fakhri juga).
//
// Use case untuk proses login.
// - Terima email dan password
// - Validasi format input (email valid, password tidak kosong)
// - Panggil AuthRepository.login()
// - Return Result<User> (success atau error)
