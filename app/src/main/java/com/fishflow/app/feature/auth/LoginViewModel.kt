// LoginViewModel.kt
// Developer: Fakhri
//
// Koneksi: ViewModel ini pakai LoginUseCase (Fakhri) dan ValidationUtils (Fakhri).
// Hasilnya menentukan navigasi ke dashboard sesuai role.
//
// ViewModel untuk halaman login.
//
// Yang perlu dilakukan:
// - Simpan LoginUiState di StateFlow
// - Fungsi onEmailChange(email) -> update state.email
// - Fungsi onPasswordChange(password) -> update state.password
// - Fungsi onLoginClick() ->
//   1. Set isLoading = true
//   2. Panggil LoginUseCase(email, password)
//   3. Jika berhasil -> set isLoginSuccess = true (nanti NavGraph arahkan ke dashboard)
//   4. Jika gagal -> set errorMessage = pesan error
//   5. Set isLoading = false
