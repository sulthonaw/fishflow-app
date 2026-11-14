// BudgetPlannerViewModel.kt
// Developer: Fatih
//
// ViewModel kalkulator anggaran wisata.
//
// Yang perlu dilakukan:
// - Ambil daftar aktivitas berdasarkan destinationId
// - Fungsi onActivityToggle(activityId) → tambah/hapus dari selectedActivityIds
// - Setiap kali selectedActivityIds berubah, panggil CalculateBudgetEstimateUseCase
// - Update totalBiaya dan catatanBelumTersedia di UiState
// - Fungsi onReset() → kosongkan semua pilihan
