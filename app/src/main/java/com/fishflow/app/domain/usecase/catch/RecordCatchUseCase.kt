// RecordCatchUseCase.kt
// Developer: Fakhri
//
// Use case untuk mencatat hasil tangkapan baru.
// - Terima: jenisIkan, beratKg, tanggalTangkap, foto (optional)
// - Validasi: berat > 0, jenis ikan tidak kosong, tanggal tidak di masa depan
// - Panggil FishCatchRepository.recordCatch()
// - Setelah berhasil, trigger update stok di ProductRepository
// - Return Result<FishCatch>
