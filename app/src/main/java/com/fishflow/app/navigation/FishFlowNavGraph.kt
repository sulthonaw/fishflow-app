// FishFlowNavGraph.kt
// Developer: Sulthon
//
// Koneksi: Sulthon yang definisikan Screen routes, jadi NavHost-nya juga dia.
// Dia juga yang setup MainActivity yang memanggil NavGraph ini.
//
// NavHost utama yang menghubungkan semua Screen.
//
// Yang perlu dilakukan:
// - Buat NavHost dengan semua composable() route
// - Start destination tergantung status login (login screen atau dashboard)
// - Setiap composable() route memanggil Screen yang sesuai
// - Pass navController untuk navigasi antar halaman
// - Kelompokkan route dengan navigation() nested graph:
//   - authGraph (login, register)
//   - sellerGraph (dashboard, catch, inventory, orders)
//   - touristGraph (home, destinations, catalog, cart, checkout, history)
//   - adminGraph (dashboard, manage)
