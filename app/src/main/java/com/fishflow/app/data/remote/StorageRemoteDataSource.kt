// StorageRemoteDataSource.kt
// Developer: Fakhri
//
// Akses ke Supabase Storage untuk upload/download foto.
//
// Yang perlu dilakukan:
// - uploadCatchPhoto(sellerId, imageBytes, fileName) → upload ke bucket "catch-photos", return URL
// - uploadProductImage(productId, imageBytes) → upload ke bucket "product-images"
// - getPublicUrl(bucket, path) → ambil URL publik foto
// - deletePhoto(bucket, path) → hapus foto (jika tangkapan dihapus)
//
// Pakai supabase.storage.
