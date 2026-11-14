// MainActivity.kt
// Developer: Sulthon
//
// Koneksi: Sulthon yang bikin NavGraph, jadi dia juga yang setup Activity
// sebagai entry point Compose dan memanggil NavGraph-nya.
//
// File ini adalah satu-satunya Activity di aplikasi (Single Activity Architecture).
//
// Yang perlu dilakukan:
// - Extends ComponentActivity()
// - Tambahkan anotasi @AndroidEntryPoint
// - Di dalam setContent { }, panggil FishFlowNavGraph sebagai entry point Compose
// - Terapkan FishFlowTheme sebagai wrapper terluar
// - Cek status login user saat pertama kali buka:
//   - Jika sudah login -> arahkan ke dashboard sesuai role (Penjual/Wisatawan/Admin)
//   - Jika belum login -> arahkan ke LoginScreen
