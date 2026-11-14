// RegisterUseCase.kt
// Developer: Fakhri
//
// Koneksi: Pasangan dari LoginUseCase. Fakhri handle seluruh auth.
//
// Use case untuk registrasi user baru.
// - Terima email, password, nama, role
// - Validasi semua input (pakai ValidationUtils yang Fakhri buat)
// - Panggil AuthRepository.register()
// - Return Result<User>
