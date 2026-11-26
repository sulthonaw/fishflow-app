package com.fishflow.app.data.repository

import com.fishflow.app.data.model.Order
import com.fishflow.app.data.model.OrderItem
import com.fishflow.app.data.remote.OrderRemoteDataSource
import com.fishflow.app.data.remote.RemoteCartItemDto
import kotlinx.coroutines.flow.Flow

class OrderRepositoryImpl(
    private val remote: OrderRemoteDataSource = OrderRemoteDataSource()
) : OrderRepository {

    override suspend fun getCartItems(buyerId: String): Result<List<RemoteCartItemDto>> =
        runCatching { remote.getCartItems(buyerId) }

    override suspend fun upsertCartItem(
        buyerId: String,
        productId: String,
        qtyKg: Double
    ): Result<Unit> = runCatching {
        remote.upsertCartItem(buyerId, productId, qtyKg)
    }

    override suspend fun removeCartItem(buyerId: String, productId: String): Result<Unit> =
        runCatching { remote.removeCartItem(buyerId, productId) }

    override suspend fun clearCart(buyerId: String): Result<Unit> =
        runCatching { remote.clearCart(buyerId) }

    override suspend fun createOrder(
        buyerId: String,
        sellerId: String,
        pickupPoint: String,
        extraFee: Double,
        items: List<OrderItem>
    ): Result<String> = runCatching {
        remote.createOrderAtomic(
            buyerId = buyerId,
            sellerId = sellerId,
            pickupPoint = pickupPoint,
            extraFee = extraFee,
            items = items
        )
    }

    override suspend fun confirmOrder(orderId: String): Result<Unit> =
        runCatching { remote.confirmOrderAtomic(orderId) }

    override suspend fun rejectOrder(orderId: String): Result<Unit> =
        runCatching { remote.rejectOrderAtomic(orderId) }

    override suspend fun completeOrder(orderId: String, kodePickup: String): Result<Unit> =
        runCatching { remote.completeOrderAtomic(orderId, kodePickup) }

    override suspend fun cancelOrder(orderId: String): Result<Unit> =
        runCatching { remote.cancelOrderAtomic(orderId) }

    override suspend fun getOrdersByBuyer(buyerId: String): Result<List<Order>> =
        runCatching { remote.getOrdersByBuyer(buyerId) }

    override suspend fun getOrdersBySeller(sellerId: String): Result<List<Order>> =
        runCatching { remote.getOrdersBySeller(sellerId) }

    override suspend fun getOrderById(orderId: String): Result<Order?> =
        runCatching { remote.getOrderById(orderId) }

    override fun observeOrderStatus(orderId: String): Flow<Order?> =
        remote.observeOrder(orderId)

    override fun observeSellerOrders(sellerId: String): Flow<List<Order>> =
        remote.observeOrdersBySeller(sellerId)

    override fun observeBuyerOrders(buyerId: String): Flow<List<Order>> =
        remote.observeOrdersByBuyer(buyerId)
}
