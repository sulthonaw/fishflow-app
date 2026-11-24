// FilterCatalogProductsUseCase.kt
// Developer: Octavia
//
// Aturan penyaringan katalog ikan (Fungsi 4). Fungsi murni tanpa akses data,
// sehingga bisa diuji langsung dengan unit test.
//
// Produk tampil jika memenuhi semua syarat:
// 1. Status aktif dan stok tersedia > 0
// 2. Wilayah sama dengan pilihan (jika wilayah dipilih)
// 3. Kategori sama dengan pilihan (jika kategori dipilih)
// 4. Nama ikan mengandung kata kunci (jika diisi)
package com.fishflow.app.domain.usecase.product

import com.fishflow.app.data.model.FishCategory
import com.fishflow.app.data.model.FishProduct

data class CatalogFilter(
    val wilayah: String? = null,
    val kategori: FishCategory? = null,
    val query: String = ""
)

class FilterCatalogProductsUseCase {

    operator fun invoke(
        products: List<FishProduct>,
        filter: CatalogFilter = CatalogFilter()
    ): List<FishProduct> {
        val wilayah = filter.wilayah?.trim().orEmpty()
        val keyword = filter.query.trim()

        return products.filter { product ->
            product.isAvailable &&
                (wilayah.isEmpty() || product.wilayah.equals(wilayah, ignoreCase = true)) &&
                (filter.kategori == null || product.kategori == filter.kategori) &&
                (keyword.isEmpty() || product.namaIkan.contains(keyword, ignoreCase = true))
        }
    }
}
