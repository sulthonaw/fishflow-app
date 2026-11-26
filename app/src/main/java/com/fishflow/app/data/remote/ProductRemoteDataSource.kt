// ProductRemoteDataSource.kt
// Developer: Octavia & Fakhri
//
// Akses ke tabel "products" di Supabase.
// Untuk tahap ini implementasi yang dipakai adalah penyimpanan in-memory
// (InMemoryProductRemoteDataSource), sama seperti data destinasi Fungsi 3.
package com.fishflow.app.data.remote

import com.fishflow.app.R
import com.fishflow.app.core.network.SupabaseClientProvider
import com.fishflow.app.data.model.FishCategory
import com.fishflow.app.data.model.FishProduct
import com.fishflow.app.data.model.ProductStatus
import com.fishflow.app.supabase.RealtimeChannels
import com.fishflow.app.supabase.Tables
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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

@Serializable
private data class SellerProfileJoinDto(
    @SerialName("nama_kapal") val namaKapal: String = ""
)

@Serializable
private data class ProfileJoinDto(
    val nama: String = "",
    @SerialName("seller_profiles") val sellerProfiles: SellerProfileJoinDto? = null
)

@Serializable
private data class ProductDto(
    val id: String = "",
    @SerialName("seller_id") val sellerId: String = "",
    @SerialName("nama_ikan") val namaIkan: String = "",
    val kategori: String = FishCategory.PELAGIS.name,
    @SerialName("harga_per_kg") val hargaPerKg: Double = 0.0,
    val wilayah: String = "",
    @SerialName("lokasi_pengambilan") val lokasiPengambilan: String = "",
    val status: String = ProductStatus.AKTIF.name,
    @SerialName("stok_tersedia") val stokTersedia: Double = 0.0,
    @SerialName("stok_teralokasi") val stokTeralokasi: Double = 0.0,
    @SerialName("batas_pengambilan") val batasPengambilan: String? = null,
    @SerialName("info_penanganan") val infoPenanganan: String? = null,
    @SerialName("foto_url") val fotoUrl: String? = null,
    @SerialName("created_at") val createdAt: String = "",
    private val profiles: ProfileJoinDto? = null
) {
    fun toDomain(): FishProduct = FishProduct(
        id = id,
        sellerId = sellerId,
        namaIkan = namaIkan,
        kategori = runCatching { FishCategory.valueOf(kategori) }.getOrDefault(FishCategory.PELAGIS),
        hargaPerKg = hargaPerKg,
        wilayah = wilayah,
        lokasiPengambilan = lokasiPengambilan,
        namaPenjual = profiles?.nama.orEmpty(),
        namaKapal = profiles?.sellerProfiles?.namaKapal.orEmpty(),
        status = runCatching { ProductStatus.valueOf(status) }.getOrDefault(ProductStatus.AKTIF),
        stokTersedia = stokTersedia,
        stokTeralokasi = stokTeralokasi,
        batasPengambilan = batasPengambilan,
        infoPenanganan = infoPenanganan,
        fotoUrl = fotoUrl,
        createdAt = createdAt
    )
}

class SupabaseProductRemoteDataSource(
    private val supabase: SupabaseClient = SupabaseClientProvider.client
) : ProductRemoteDataSource {

    private val selectColumns = Columns.raw("*, profiles(nama, seller_profiles(nama_kapal))")

    override suspend fun getAllProducts(): List<FishProduct> =
        supabase.postgrest[Tables.PRODUCTS]
            .select(columns = selectColumns) {
                order("nama_ikan", Order.ASCENDING)
            }
            .decodeList<ProductDto>()
            .map { it.toDomain() }

    override suspend fun getActiveProducts(): List<FishProduct> =
        supabase.postgrest[Tables.PRODUCTS]
            .select(columns = selectColumns) {
                filter {
                    eq("status", ProductStatus.AKTIF.name)
                }
                order("nama_ikan", Order.ASCENDING)
            }
            .decodeList<ProductDto>()
            .map { it.toDomain() }

    override suspend fun getProductsByRegion(wilayah: String): List<FishProduct> =
        supabase.postgrest[Tables.PRODUCTS]
            .select(columns = selectColumns) {
                filter {
                    ilike("wilayah", wilayah.trim())
                }
                order("nama_ikan", Order.ASCENDING)
            }
            .decodeList<ProductDto>()
            .map { it.toDomain() }

    override suspend fun getProductsBySeller(sellerId: String): List<FishProduct> =
        supabase.postgrest[Tables.PRODUCTS]
            .select(columns = selectColumns) {
                filter {
                    eq("seller_id", sellerId)
                }
                order("nama_ikan", Order.ASCENDING)
            }
            .decodeList<ProductDto>()
            .map { it.toDomain() }

    override suspend fun getProductById(id: String): FishProduct? =
        supabase.postgrest[Tables.PRODUCTS]
            .select(columns = selectColumns) {
                filter {
                    eq("id", id)
                }
            }
            .decodeSingleOrNull<ProductDto>()
            ?.toDomain()

    override suspend fun updatePrice(productId: String, hargaBaru: Double) {
        supabase.postgrest[Tables.PRODUCTS]
            .update({
                set("harga_per_kg", hargaBaru)
            }) {
                filter {
                    eq("id", productId)
                }
            }
    }

    override suspend fun updateStock(productId: String, stokBaru: Double) {
        supabase.postgrest[Tables.PRODUCTS]
            .update({
                set("stok_tersedia", stokBaru)
            }) {
                filter {
                    eq("id", productId)
                }
            }
    }

    override suspend fun updateStatus(productId: String, isActive: Boolean) {
        val status = if (isActive) ProductStatus.AKTIF.name else ProductStatus.NONAKTIF.name
        supabase.postgrest[Tables.PRODUCTS]
            .update({
                set("status", status)
            }) {
                filter {
                    eq("id", productId)
                }
            }
    }

    override fun subscribeToProductChanges(): Flow<List<FishProduct>> = flow {
        emit(getAllProducts())
        val channel = supabase.channel(RealtimeChannels.PRODUCTS_CHANNEL)
        val changes = channel.postgresChangeFlow<PostgresAction>(schema = "public") {
            table = Tables.PRODUCTS
        }
        channel.subscribe()
        try {
            emitAll(changes.map { getAllProducts() })
        } finally {
            runCatching { channel.unsubscribe() }
        }
    }
}
