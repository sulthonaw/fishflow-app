// OrderRemoteDataSource.kt
// Developer: Sulthon
//
// Akses ke tabel "orders" dan "order_items" di Supabase.
//
// Yang perlu dilakukan:
// - createOrder(order, items) → simpan pesanan baru + item-itemnya (status: MENUNGGU)
// - getOrdersByBuyer(buyerId) → riwayat pesanan wisatawan
// - getOrdersBySeller(sellerId) → pesanan masuk untuk penjual
// - getOrderById(orderId) → detail pesanan + items-nya (join)
// - updateOrderStatus(orderId, status) → ubah status pesanan
// - allocateStock(orderId) → kunci stok saat penjual konfirmasi (kurangi stokTeralokasi)
// - completeOrder(orderId) → catat penjualan selesai, update stok final
// - rejectOrder(orderId, alasan) → tolak pesanan
// - cancelOrder(orderId) → batalkan oleh wisatawan
// - subscribeToOrderUpdates(orderId) → Realtime listener status pesanan
//
// PENTING: Untuk allocateStock, gunakan Supabase RPC (database function) 
// supaya pengecekan stok dan pengurangan stok terjadi secara atomic (tidak race condition).
