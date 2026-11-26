package com.fishflow.app.data.repository

import com.fishflow.app.data.model.Order
import com.fishflow.app.data.model.OrderItem
import com.fishflow.app.data.remote.RemoteCartItemDto
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    suspend fun getCartItems(buyerId: String): Result<List<RemoteCartItemDto>>
    suspend fun upsertCartItem(buyerId: String, productId: String, qtyKg: Double): Result<Unit>
    suspend fun removeCartItem(buyerId: String, productId: String): Result<Unit>
    suspend fun clearCart(buyerId: String): Result<Unit>

    suspend fun createOrder(
        buyerId: String,
        sellerId: String,
        pickupPoint: String,
        extraFee: Double,
        items: List<OrderItem>
    ): Result<String>

    suspend fun confirmOrder(orderId: String): Result<Unit>
    suspend fun rejectOrder(orderId: String): Result<Unit>
    suspend fun completeOrder(orderId: String, kodePickup: String): Result<Unit>
    suspend fun cancelOrder(orderId: String): Result<Unit>

    suspend fun getOrdersByBuyer(buyerId: String): Result<List<Order>>
    suspend fun getOrdersBySeller(sellerId: String): Result<List<Order>>
    suspend fun getOrderById(orderId: String): Result<Order?>

    fun observeOrderStatus(orderId: String): Flow<Order?>
    fun observeSellerOrders(sellerId: String): Flow<List<Order>>
    fun observeBuyerOrders(buyerId: String): Flow<List<Order>>
}
