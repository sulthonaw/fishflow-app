// OrderTrackingScreen.kt
// Developer: Sulthon
//
// Tracking status pesanan secara real-time.
//
// UI (sesuai mockup):
// - Kartu status pengambilan di dermaga/TPI
// - Stepper visual 4 tahap:
//   1. Pesanan Diterima
//   2. Stok Dialokasikan
//   3. Siap Diambil di Dermaga
//   4. Penyerahan Selesai
// - Tahap yang sudah dilalui ditandai centang hijau
// - Kode QR/token pengambilan (muncul setelah status SIAP_DIAMBIL)
// - Info kontak penjual
// - Realtime update — status berubah otomatis tanpa refresh
