// ProductRemoteDataSource.kt
// Developer: Octavia & Fakhri
//
// Akses ke tabel "products" di Supabase.
// Untuk tahap ini implementasi yang dipakai adalah penyimpanan in-memory
// (InMemoryProductRemoteDataSource), sama seperti data destinasi Fungsi 3.
package com.fishflow.app.data.remote

import com.fishflow.app.R
import com.fishflow.app.data.model.FishCategory
import com.fishflow.app.data.model.FishProduct
import com.fishflow.app.data.model.ProductStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface ProductRemoteDataSource {
    suspend fun getAllProducts(): List<FishProduct>
    suspend fun getActiveProducts(): List<FishProduct>
    suspend fun getProductsByRegion(wilayah: String): List<FishProduct>
    suspend fun getProductsBySeller(sellerId: String): List<FishProduct>
    suspend fun getProductById(id: String): FishProduct?
    suspend fun updatePrice(productId: String, hargaBaru: Double)
    suspend fun updateStock(productId: String, stokBaru: Double)
    suspend fun updateStatus(productId: String, isActive: Boolean)
    fun subscribeToProductChanges(): Flow<List<FishProduct>>
}

// Data contoh. Wilayah mengikuti Destination.wilayah milik Fungsi 3. Produk nonaktif dan
// produk tanpa stok sengaja disertakan untuk menguji aturan penyaringan katalog.
object InMemoryProductRemoteDataSource : ProductRemoteDataSource {

    private val products: MutableList<FishProduct> = seedProducts().toMutableList()

    override suspend fun getAllProducts(): List<FishProduct> =
        products.sortedBy { it.namaIkan }

    override suspend fun getActiveProducts(): List<FishProduct> =
        products.filter { it.status == ProductStatus.AKTIF }.sortedBy { it.namaIkan }

    override suspend fun getProductsByRegion(wilayah: String): List<FishProduct> =
        products
            .filter { it.wilayah.equals(wilayah, ignoreCase = true) }
            .sortedBy { it.namaIkan }

    override suspend fun getProductsBySeller(sellerId: String): List<FishProduct> =
        products
            .filter { it.sellerId == sellerId }
            .sortedBy { it.namaIkan }

    override suspend fun getProductById(id: String): FishProduct? =
        products.firstOrNull { it.id == id }

    override suspend fun updatePrice(productId: String, hargaBaru: Double) {
        val index = products.indexOfFirst { it.id == productId }
        if (index != -1) {
            products[index] = products[index].copy(hargaPerKg = hargaBaru)
        }
    }

    override suspend fun updateStock(productId: String, stokBaru: Double) {
        val index = products.indexOfFirst { it.id == productId }
        if (index != -1) {
            products[index] = products[index].copy(stokTersedia = stokBaru)
        }
    }

    override suspend fun updateStatus(productId: String, isActive: Boolean) {
        val index = products.indexOfFirst { it.id == productId }
        if (index != -1) {
            val newStatus = if (isActive) ProductStatus.AKTIF else ProductStatus.NONAKTIF
            products[index] = products[index].copy(status = newStatus)
        }
    }

    override fun subscribeToProductChanges(): Flow<List<FishProduct>> =
        flowOf(products.toList())

    private fun seedProducts(): List<FishProduct> = listOf(
        FishProduct(
            id = "prod-tuna-01",
            sellerId = "seller-baruna",
            namaIkan = "Tuna Sirip Kuning Super",
            kategori = FishCategory.PELAGIS,
            hargaPerKg = 45_000.0,
            wilayah = "Tambakrejo",
            lokasiPengambilan = "TPI Tambakrejo, Pos 3",
            namaPenjual = "Nelayan Baruna",
            namaKapal = "KM Penjual Baruna VI",
            batasPengambilan = "17:00 WIB",
            infoPenanganan = "Disimpan di cold storage",
            imageRes = R.drawable.img_tuna_super
        ),
        FishProduct(
            id = "prod-kakap-02",
            sellerId = "seller-samudera",
            namaIkan = "Kakap Merah Segar",
            kategori = FishCategory.KARANG,
            hargaPerKg = 65_000.0,
            wilayah = "Tambakrejo",
            lokasiPengambilan = "TPI Tambakrejo, Pos 1",
            namaPenjual = "Nelayan Samudera",
            namaKapal = "KM Samudera 02",
            batasPengambilan = "16:00 WIB",
            infoPenanganan = "Disimpan dengan es curah",
            imageRes = R.drawable.img_kakap_merah
        ),
        FishProduct(
            id = "prod-cumi-03",
            sellerId = "seller-bahari",
            namaIkan = "Cumi-cumi Segar",
            kategori = FishCategory.CUMI_GURITA,
            hargaPerKg = 55_000.0,
            wilayah = "Serang",
            lokasiPengambilan = "Dermaga Pantai Serang",
            namaPenjual = "Nelayan Bahari",
            namaKapal = "KM Bahari 02",
            batasPengambilan = "15:00 WIB",
            infoPenanganan = "Disimpan dengan es curah"
        ),
        FishProduct(
            id = "prod-lobster-04",
            sellerId = "seller-bahari",
            namaIkan = "Lobster Pasir",
            kategori = FishCategory.UDANG_LOBSTER,
            hargaPerKg = 250_000.0,
            wilayah = "Serang",
            lokasiPengambilan = "Dermaga Pantai Serang",
            namaPenjual = "Nelayan Bahari",
            namaKapal = "KM Bahari 02",
            batasPengambilan = "15:00 WIB"
        ),
        FishProduct(
            id = "prod-tongkol-05",
            sellerId = "seller-mina",
            namaIkan = "Tongkol",
            kategori = FishCategory.PELAGIS,
            hargaPerKg = 28_000.0,
            wilayah = "Serang",
            lokasiPengambilan = "Dermaga Pantai Serang",
            namaPenjual = "Nelayan Mina"
        ),
        FishProduct(
            id = "prod-kerapu-06",
            sellerId = "seller-samudera",
            namaIkan = "Kerapu Macan",
            kategori = FishCategory.KARANG,
            hargaPerKg = 80_000.0,
            wilayah = "Tambakrejo",
            lokasiPengambilan = "TPI Tambakrejo, Pos 1",
            namaPenjual = "Nelayan Samudera",
            namaKapal = "KM Samudera 02"
        ),
        FishProduct(
            id = "prod-layur-07",
            sellerId = "seller-jolo",
            namaIkan = "Layur",
            kategori = FishCategory.PELAGIS,
            hargaPerKg = 35_000.0,
            wilayah = "Jolosutro",
            lokasiPengambilan = "Pos Nelayan Jolosutro",
            namaPenjual = "Nelayan Jolosutro",
            status = ProductStatus.NONAKTIF
        ),
        FishProduct(
            id = "prod-udang-08",
            sellerId = "seller-jolo",
            namaIkan = "Udang Jerbung",
            kategori = FishCategory.UDANG_LOBSTER,
            hargaPerKg = 90_000.0,
            wilayah = "Jolosutro",
            lokasiPengambilan = "Pos Nelayan Jolosutro",
            namaPenjual = "Nelayan Jolosutro"
        )
    )
}
