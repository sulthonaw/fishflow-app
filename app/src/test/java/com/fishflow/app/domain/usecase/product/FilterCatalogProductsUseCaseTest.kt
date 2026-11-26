package com.fishflow.app.domain.usecase.product

import com.fishflow.app.data.model.FishCategory
import com.fishflow.app.data.model.FishProduct
import com.fishflow.app.data.model.ProductStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FilterCatalogProductsUseCaseTest {

    private val filterProducts = FilterCatalogProductsUseCase()

    @Test
    fun `tanpa filter menampilkan semua produk aktif yang punya stok`() {
        val products = listOf(
            product("tuna"),
            product("cumi", wilayah = "Serang", kategori = FishCategory.CUMI_GURITA)
        )

        assertEquals(listOf("tuna", "cumi"), filterProducts(products).map { it.id })
    }

    @Test
    fun `produk nonaktif tersaring`() {
        val products = listOf(
            product("tuna"),
            product("layur", status = ProductStatus.NONAKTIF)
        )

        assertEquals(listOf("tuna"), filterProducts(products).map { it.id })
    }

    @Test
    fun `produk dengan stok nol atau minus tersaring`() {
        val products = listOf(
            product("tuna", stok = 0.5),
            product("kerapu", stok = 0.0),
            product("udang", stok = -2.0)
        )

        assertEquals(listOf("tuna"), filterProducts(products).map { it.id })
    }

    @Test
    fun `filter wilayah hanya menampilkan produk di wilayah itu`() {
        val products = listOf(
            product("tuna", wilayah = "Tambakrejo"),
            product("cumi", wilayah = "Serang")
        )

        val result = filterProducts(products, CatalogFilter(wilayah = " serang "))

        assertEquals(listOf("cumi"), result.map { it.id })
    }

    @Test
    fun `filter kategori hanya menampilkan kategori itu`() {
        val products = listOf(
            product("tuna", kategori = FishCategory.PELAGIS),
            product("kakap", kategori = FishCategory.KARANG)
        )

        val result = filterProducts(products, CatalogFilter(kategori = FishCategory.KARANG))

        assertEquals(listOf("kakap"), result.map { it.id })
    }

    @Test
    fun `wilayah kategori dan kata kunci digabung`() {
        val products = listOf(
            product("Tuna", wilayah = "Tambakrejo", kategori = FishCategory.PELAGIS),
            product("Tongkol", wilayah = "Tambakrejo", kategori = FishCategory.PELAGIS),
            product("Tuna Serang", wilayah = "Serang", kategori = FishCategory.PELAGIS),
            product("Tuna Karang", wilayah = "Tambakrejo", kategori = FishCategory.KARANG)
        )

        val result = filterProducts(
            products,
            CatalogFilter(
                wilayah = "Tambakrejo",
                kategori = FishCategory.PELAGIS,
                query = "tuna"
            )
        )

        assertEquals(listOf("Tuna"), result.map { it.id })
    }

    @Test
    fun `kata kunci juga mencocokkan wilayah`() {
        val products = listOf(
            product("tuna", wilayah = "Tambakrejo"),
            product("cumi", wilayah = "Serang")
        )

        assertEquals(
            listOf("cumi"),
            filterProducts(products, CatalogFilter(query = "serang")).map { it.id }
        )
        assertEquals(
            listOf("cumi"),
            filterProducts(products, CatalogFilter(query = "Pantai Serang")).map { it.id }
        )
        assertTrue(filterProducts(products, CatalogFilter(query = "pantai")).isEmpty())
    }

    @Test
    fun `hasil kosong saat tidak ada yang cocok`() {
        val products = listOf(product("tuna", wilayah = "Tambakrejo"))

        assertTrue(filterProducts(products, CatalogFilter(wilayah = "Jolosutro")).isEmpty())
        assertTrue(filterProducts(emptyList()).isEmpty())
    }

    private fun product(
        id: String,
        wilayah: String = "Tambakrejo",
        kategori: FishCategory = FishCategory.PELAGIS,
        status: ProductStatus = ProductStatus.AKTIF,
        stok: Double = 5.0
    ) = FishProduct(
        id = id,
        sellerId = "seller-1",
        namaIkan = id,
        kategori = kategori,
        hargaPerKg = 10_000.0,
        wilayah = wilayah,
        lokasiPengambilan = "TPI",
        namaPenjual = "Nelayan",
        status = status,
        stokTersedia = stok
    )
}
