// GetProductsByRegionUseCase.kt
// Developer: Octavia
//
// Ambil produk ikan yang tersedia, dengan atau tanpa filter wilayah wisata.
// - wilayah diisi  -> ProductRepository.getByRegion(wilayah)
// - wilayah kosong -> ProductRepository.getAll()
// - Hasil hanya produk aktif dengan stok > 0
package com.fishflow.app.domain.usecase.product

import com.fishflow.app.data.model.FishProduct
import com.fishflow.app.data.repository.ProductRepository

class GetProductsByRegionUseCase(
    private val repository: ProductRepository,
    private val filterProducts: FilterCatalogProductsUseCase = FilterCatalogProductsUseCase()
) {
    suspend operator fun invoke(wilayah: String? = null): Result<List<FishProduct>> {
        val result = if (wilayah.isNullOrBlank()) {
            repository.getAll()
        } else {
            repository.getByRegion(wilayah)
        }
        return result.map { products -> filterProducts(products, CatalogFilter(wilayah = wilayah)) }
    }
}
