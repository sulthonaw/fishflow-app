// FishFlowApplication.kt
// Developer: Sulthon
//
// Koneksi: File ini fondasi app. Sulthon setup seluruh infrastruktur
// (DI, Supabase, navigasi), jadi Application class juga tanggung jawabnya.
//
// File ini adalah Application class utama untuk FishFlow.
//
// Yang perlu dilakukan:
// - Extends Application()
// - Tambahkan anotasi @HiltAndroidApp supaya Hilt bisa bekerja
// - Tidak perlu isi apa-apa di dalam class-nya, Hilt yang handle sisanya
// - Class ini didaftarkan di AndroidManifest.xml pada atribut android:name
