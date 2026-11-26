package com.fishflow.app.domain.usecase.product

import com.fishflow.app.data.repository.ProductRepository

class UpdateProductPriceUseCase(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(
        productId: String,
        hargaBaru: Double
    ): Result<Unit> {

        if (hargaBaru <= 0) {
            return Result.failure(
                IllegalArgumentException("Harga harus lebih dari 0.")
            )
        }

        return try {
            productRepository.updatePrice(
                productId = productId,
                hargaBaru = hargaBaru
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
