// AddCatchViewModel.kt
// Developer: Fakhri
//
// ViewModel untuk form pencatatan tangkapan baru.
//
// Yang perlu dilakukan:
// - Fungsi update setiap field: onJenisIkanChange, onBeratChange, onTanggalChange, onFotoSelected
// - Fungsi onSave() →
//   1. Validasi: jenis tidak kosong, berat > 0, tanggal valid
//   2. Jika ada foto → upload ke Storage dulu, dapatkan URL
//   3. Panggil RecordCatchUseCase
//   4. Jika berhasil → set isSaved = true, navigasi balik ke inventory
//   5. Jika gagal → set errorMessage
