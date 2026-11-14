// CalculateAvailableStockUseCase.kt
// Developer: Fakhri
//
// Hitung stok ikan yang benar-benar tersedia untuk dipesan.
// Rumus: Stok Tersedia = Total Tangkapan - Total Terjual - Total Teralokasi
//
// - Ambil data dari FishCatchRepository dan OrderRepository
// - Hitung per jenis ikan per penjual
// - Return Map<productId, stokTersedia>
