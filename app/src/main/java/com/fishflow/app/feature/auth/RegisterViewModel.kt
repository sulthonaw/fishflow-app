// RegisterViewModel.kt
// Developer: Fakhri
//
// Koneksi: Pakai RegisterUseCase (Fakhri) dan ValidationUtils (Fakhri).
// User pilih role di sini, yang menentukan alur seluruh app-nya.
//
// ViewModel untuk halaman register.
// - Fungsi untuk update setiap field (onNamaChange, onEmailChange, dll)
// - Fungsi onRoleSelect(role) -> wisatawan atau penjual
// - Fungsi onRegisterClick() -> validasi semua field, panggil RegisterUseCase
// - Validasi: password == confirmPassword, email format benar, nama tidak kosong
