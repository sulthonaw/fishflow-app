// CurrencyFormatter.kt
// Developer: Fakhri
//
// Koneksi: Fakhri tampilkan harga di inventory dan dashboard (omset Rp 680.000).
// Format Rupiah ini juga dipakai di checkout, katalog, dan budget planner.
//
// Format angka ke format Rupiah.
//
// Yang perlu dilakukan:
// - Fungsi formatRupiah(amount: Double) -> "Rp 680.000"
// - Pakai NumberFormat dengan Locale Indonesia
// - Handle angka 0 dan angka negatif
