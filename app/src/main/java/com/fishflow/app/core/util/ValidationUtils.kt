// ValidationUtils.kt
// Developer: Fakhri
//
// Koneksi: Fakhri pakai validasi di form tangkapan (berat > 0) dan update harga.
// Dia juga handle auth (validasi email, password). Fungsi ini dipakai lintas fitur.
//
// Kumpulan fungsi validasi input yang dipakai di banyak tempat.
//
// Yang perlu dilakukan:
// - validateEmail(email) -> cek format email valid
// - validatePassword(password) -> minimal 6 karakter
// - validateWeight(berat) -> harus angka positif (berat > 0)
// - validatePrice(harga) -> harus angka positif (harga > 0)
// - validateQuantity(qty, stokTersedia) -> qty > 0 dan qty <= stokTersedia
// - Setiap fungsi return String? (null = valid, String = pesan error)
