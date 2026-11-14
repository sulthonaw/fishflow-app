// FishProduct.kt
// Developer: Octavia
//
// Koneksi: Octavia memegang fitur katalog produk ikan (Fungsi 4). Model FishProduct
// adalah entitas utama yang dikonsumsi langsung oleh katalog wisatawan, filter wilayah,
// dan detail produk ikan miliknya.
//
// Data class untuk produk ikan yang dijual.
// Field: id, sellerId, namaIkan, hargaPerKg (Double), stokTersedia (Double),
//        stokTeralokasi (Double), status (aktif/nonaktif), wilayah (String)
// Mapping ke tabel "products" di Supabase.
// Stok riil yang bisa dipesan = stokTersedia - stokTeralokasi
