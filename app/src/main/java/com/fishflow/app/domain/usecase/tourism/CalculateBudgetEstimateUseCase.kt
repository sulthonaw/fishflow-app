// CalculateBudgetEstimateUseCase.kt
// Developer: Fatih
//
// Hitung estimasi anggaran wisata berdasarkan aktivitas yang dipilih.
//
// Langkah:
// 1. Terima List<Activity> yang dipilih wisatawan
// 2. Pisahkan aktivitas yang biayanya sudah pasti vs belum tersedia
// 3. Jumlahkan biaya yang sudah pasti
// 4. Buat catatan untuk aktivitas yang biayanya belum diketahui
// 5. Return BudgetEstimate (totalBiaya + daftar catatan)
