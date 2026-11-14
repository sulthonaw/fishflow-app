// LoginScreen.kt
// Developer: Fakhri
//
// Koneksi: Halaman pertama yang user lihat. Setelah login berhasil,
// masuk ke SellerDashboard (Fakhri) atau TouristHome (Octavia) tergantung role.
//
// Halaman login. Menggunakan State Hoisting.
//
// Pattern:
// - LoginScreen (stateful) -> inject LoginViewModel, collect uiState
// - LoginContent (stateless) -> terima uiState + lambda callbacks
//
// UI yang ditampilkan:
// - Logo FishFlow
// - TextField email
// - TextField password (hidden)
// - Tombol "Masuk"
// - Loading indicator saat proses login
// - Pesan error jika gagal
// - Link "Belum punya akun? Daftar" -> navigasi ke RegisterScreen
