// Screen.kt
// Developer: Sulthon
//
// Koneksi: Sulthon handle fitur di sisi seller (orders) DAN tourist
// (cart, checkout, order history), jadi dia paham alur navigasi keduanya.
// Ditambah dia yang setup NavGraph.
//
// Definisi semua route/halaman di aplikasi.
//
// Yang perlu dilakukan:
// - Buat sealed class atau enum Screen dengan route string
// - Contoh route:
//   - Auth: "login", "register"
//   - Seller: "seller_dashboard", "add_catch", "inventory", "product_detail/{id}",
//             "update_price/{id}", "seller_orders", "seller_order_detail/{id}"
//   - Tourist: "tourist_home", "destinations", "destination_detail/{id}",
//              "budget_planner/{destinationId}", "fish_catalog", "fish_detail/{id}",
//              "cart", "checkout", "order_history", "order_tracking/{orderId}"
//   - Admin: "admin_dashboard", "manage_destinations", "manage_users"
// - Untuk route yang butuh argumen, definisikan navArgument-nya
