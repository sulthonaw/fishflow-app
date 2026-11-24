
package com.fishflow.app.data.remote

import com.fishflow.app.data.model.FishProduct
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class ProductRemoteDataSource(
    initialProducts: List<FishProduct> = emptyList()
) {

    // Penyimpanan sementara produk dalam memori
    private val _products =
        MutableStateFlow(initialProducts)

    suspend fun getActiveProducts(): List<FishProduct> {
        return _products.value.filter { product ->
            product.status && product.stokRiil > 0.0
        }
    }

    suspend fun getProductsByRegion(
        wilayah: String
    ): List<FishProduct> {
        return _products.value.filter { product ->
            product.status &&
                product.stokRiil > 0.0 &&
                product.wilayah.equals(
                    wilayah.trim(),
                    ignoreCase = true
                )
        }
    }

    suspend fun getProductsBySeller(
        sellerId: String
    ): List<FishProduct> {
        return _products.value.filter { product ->
            product.sellerId == sellerId
        }
    }

    suspend fun getProductById(
        id: String
    ): FishProduct? {
        return _products.value.find { product ->
            product.id == id
        }
    }

    suspend fun updatePrice(
        productId: String,
        hargaBaru: Double
    ) {
        require(hargaBaru.isFinite() && hargaBaru >= 0.0) {
            "Harga harus berupa angka valid dan tidak negatif."
        }

        updateProduct(productId) { product ->
            product.copy(hargaPerKg = hargaBaru)
        }
    }

    suspend fun updateStock(
        productId: String,
        stokBaru: Double
    ) {
        require(stokBaru.isFinite() && stokBaru >= 0.0) {
            "Stok harus berupa angka valid dan tidak negatif."
        }

        updateProduct(productId) { product ->
            product.copy(stokTersedia = stokBaru)
        }
    }

    suspend fun updateStatus(
        productId: String,
        isActive: Boolean
    ) {
        updateProduct(productId) { product ->
            product.copy(status = isActive)
        }
    }

    fun subscribeToProductChanges(): Flow<List<FishProduct>> {
        return _products
    }

    private fun updateProduct(
        productId: String,
        transform: (FishProduct) -> FishProduct
    ) {
        val currentProducts = _products.value

        val index = currentProducts.indexOfFirst { product ->
            product.id == productId
        }

        require(index != -1) {
            "Produk dengan ID $productId tidak ditemukan."
        }

        _products.value = currentProducts.toMutableList().apply {
            this[index] = transform(this[index])
        }
    }
}
