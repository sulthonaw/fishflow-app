# FishFlow App

![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202026.02.01-4285F4?logo=jetpackcompose&logoColor=white)
![Material 3](https://img.shields.io/badge/Material%203-Design-00897B)
![Min SDK](https://img.shields.io/badge/Min%20SDK-24-3DDC84?logo=android&logoColor=white)
![Target SDK](https://img.shields.io/badge/Target%20SDK-37-3DDC84?logo=android&logoColor=white)
![Java](https://img.shields.io/badge/JVM-21-ED8B00?logo=openjdk&logoColor=white)

FishFlow adalah aplikasi Android berbasis Kotlin dan Jetpack Compose yang mempertemukan nelayan atau penjual ikan langsung dengan wisatawan di kawasan pesisir. Melalui satu aplikasi, wisatawan bisa melihat katalog hasil tangkapan segar, memesan ikan dari kapal, memantau status pengambilan di dermaga, sekaligus merencanakan kunjungan wisata beserta perkiraan biayanya. Di sisi lain, penjual dapat mencatat hasil tangkapan harian, mengatur stok dan harga, serta memproses pesanan masuk, sementara admin bertugas mengelola daftar destinasi wisata, aktivitas, dan akun pengguna.

---

## Arsitektur Proyek

Proyek ini memakai pola **Clean Architecture** yang dipadukan dengan **MVVM** dan **Single-Activity Navigation Compose**. Struktur kode utama di dalam `app/src/main/java/com/fishflow/app/` dibagi per folder berdasarkan tanggung jawabnya:

- `core/` — Berisi fondasi bersama yang dipakai lintas fitur, seperti pengaturan dependensi (`di`), konfigurasi jaringan (`network`), tema warna, tipografi, komponen UI pakai ulang (`ui`), dan fungsi bantu seperti format Rupiah, tanggal, serta validasi (`util`).
- `data/` — Lapisan pengelolaan data aplikasi, mencakup model data (`model`), penyimpanan lokal (`local`), sumber data jarak jauh (`remote`), serta implementasi repository (`repository`) sebagai penghubung ke lapisan domain.
- `domain/` — Lapisan aturan bisnis aplikasi yang berisi kumpulan *use case* (`usecase`) untuk alur autentikasi, tangkapan ikan, produk, pesanan, dan estimasi anggaran wisata.
- `feature/` — Berisi seluruh tampilan layar dan pengelola state (*ViewModel* & *UiState*) yang dikelompokkan berdasarkan peran pengguna:
  - `auth/` — Alur masuk, pendaftaran akun dengan pilihan peran, dan halaman profil.
  - `tourist/` — Fitur wisatawan mulai dari beranda, katalog ikan, keranjang, checkout, riwayat dan pelacakan pesanan, hingga daftar destinasi dan perencana anggaran.
  - `seller/` — Fitur penjual untuk melihat ringkasan bisnis, mencatat tangkapan, mengelola stok dan harga, serta menerima atau menyerahkan pesanan.
  - `admin/` — Fitur admin untuk mengelola data destinasi, aktivitas wisata, dan daftar akun pengguna.
- `navigation/` — Mengatur seluruh definisi rute halaman, menu navigasi bawah, dan alur perpindahan antar layar menggunakan Jetpack Navigation Compose.
- `supabase/` — Menyimpan konfigurasi, daftar tabel, saluran *realtime*, dan catatan kebijakan akses untuk integrasi backend Supabase.

---

## Cara Instalasi

### Prasyarat
- **Android Studio** versi terbaru
- **JDK 21** (sudah disesuaikan pada konfigurasi daemon Gradle proyek)
- **Android SDK** dengan minimal API 24 (Android 7.0)

### Langkah-langkah
1. **Unduh repositori**
   ```bash
   git clone https://github.com/sulthonaw/fishflow-app.git
   cd fishflow-app
   ```

2. **Buka proyek di Android Studio**
   - Pilih **Open** dan arahkan ke folder `fishflow-app`.
   - Tunggu proses *Gradle Sync* selesai hingga seluruh dependensi terunduh.

3. **Build atau jalankan aplikasi**
   - Lewat Android Studio: pilih emulator atau perangkat Android yang terhubung, lalu klik tombol **Run**.
   - Atau lewat terminal:
     ```powershell
     # Windows
     .\gradlew.bat :app:assembleDebug

     # macOS / Linux
     ./gradlew :app:assembleDebug
     ```

4. **Coba alur aplikasi**
   - Saat aplikasi terbuka di halaman masuk, tersedia tombol isi cepat akun demo untuk menguji tiap peran (password default `password123`):
     - **Wisatawan**: `wisatawan@fishflow.com`
     - **Penjual**: `seller@fishflow.com`
     - **Admin**: `admin@fishflow.com`
   - Anda juga bisa mendaftarkan akun baru langsung dari halaman daftar dan memilih peran yang diinginkan.
