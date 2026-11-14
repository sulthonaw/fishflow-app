// CartUiState.kt
// Developer: Sulthon
//
// State untuk halaman keranjang belanja.
// Field: items (List<CartItem> — productId, namaIkan, qty, hargaSatuan, subtotal),
//        totalHarga (Double), isStockValid (Boolean — semua item stoknya cukup),
//        stockErrors (Map<productId, String> — pesan error per item jika stok kurang),
//        isLoading, isEmpty
