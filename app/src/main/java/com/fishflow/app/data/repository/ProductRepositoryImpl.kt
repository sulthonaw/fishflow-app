
package com.fishflow.app.data.repository

import com.fishflow.app.data.model.FishProduct
import com.fishflow.app.data.remote.ProductRemoteDataSource
import kotlinx.coroutines.flow.Flow

class ProductRepositoryImpl(
    private val remoteDataSource: ProductRemoteDataSource
) : ProductRepository {

    override suspend fun getActiveProducts(): List<FishProduct> {
        return remoteDataSource.getActiveProducts()
    }

    override suspend fun getProductsByRegion(
        wilayah: String
    ): List<FishProduct> {
        return remoteDataSource.getProductsByRegion(wilayah)
    }

    override suspend fun getProductsBySeller(
        sellerId: String
    ): List<FishProduct> {
        return remoteDataSource.getProductsBySeller(sellerId)
    }

    override suspend fun getProductById(
        id: String
    ): FishProduct? {
        return remoteDataSource.getProductById(id)
    }

    override suspend fun updatePrice(
        productId: String,
        hargaBaru: Double
    ) {
        remoteDataSource.updatePrice(productId, hargaBaru)
    }

    override suspend fun updateStock(
        productId: String,
        stokBaru: Double
    ) {
        remoteDataSource.updateStock(productId, stokBaru)
    }

    override suspend fun updateStatus(
        productId: String,
        isActive: Boolean
    ) {
        remoteDataSource.updateStatus(productId, isActive)
    }

    override fun observeProducts(): Flow<List<FishProduct>> {
        return remoteDataSource.subscribeToProductChanges()
    }
}
