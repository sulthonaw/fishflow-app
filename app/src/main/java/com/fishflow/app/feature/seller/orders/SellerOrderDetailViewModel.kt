// SellerOrderDetailViewModel.kt
// Developer: Sulthon
//
// ViewModel detail pesanan.
// - Ambil detail order + items by orderId
// - Fungsi onConfirmOrder() → panggil ConfirmOrderUseCase (cek stok tahap 2 + alokasi)
// - Fungsi onRejectOrder() → panggil RejectOrderUseCase
// - Fungsi onCompleteOrder() → panggil CompleteOrderUseCase (serah terima selesai)
// - Handle error jika stok sudah habis saat konfirmasi
